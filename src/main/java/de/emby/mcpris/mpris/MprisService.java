package de.emby.mcpris.mpris;

import de.emby.mcpris.Mcpris;
import de.emby.mcpris.utils.Song;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.connections.impl.DBusConnectionBuilder;
import org.freedesktop.dbus.interfaces.DBus;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.messages.MethodCall;
import org.freedesktop.dbus.types.Variant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public final class MprisService {

    private static final String BUS_NAME_PREFIX = "org.mpris.MediaPlayer2.";
    private static final String OBJECT_PATH = "/org/mpris/MediaPlayer2";
    private static final String PLAYER_INTERFACE = "org.mpris.MediaPlayer2.Player";

    private static final String DBUS_BUS_NAME = "org.freedesktop.DBus";
    private static final String DBUS_OBJECT_PATH ="/org/freedesktop/DBus";

    private static final Semaphore WAKEUP = new Semaphore(0);

    private static volatile boolean running;
    private static volatile DBusConnection connection;
    private static volatile String selectedPlayerName;
    private static volatile boolean currentPlaying;

    private static Thread worker;

    private MprisService() {}

    @DBusInterfaceName(PLAYER_INTERFACE)
    public interface PlayerControl extends DBusInterface {
        void Play();
        void Pause();
        void PlayPause();
        void Stop();
        void Next();
        void Previous();
    }

    public static synchronized void start() {
        if (running) {
            return;
        }

        running = true;
        WAKEUP.drainPermits();

        worker = new Thread(MprisService::run, "mcpris-mpris");
        worker.setDaemon(true);
        worker.start();
    }

    public static synchronized void stop() {
        if (!running) {
            return;
        }

        running = false;
        WAKEUP.release();
        worker = null;
    }

    private static void run() {
        DBusConnection conn = null;

        try {
            MethodCall.setDefaultTimeout(5_000L);

            conn = DBusConnectionBuilder.forSessionBus().build();
            connection = conn;

            conn.addSigHandler(Properties.PropertiesChanged.class, MprisService::onPropertiesChanged);
            conn.addSigHandler(DBus.NameOwnerChanged.class, MprisService::onNameOwnerChanged);

            refresh(conn);
            Mcpris.LOGGER.info("MPRIS: connected to the session bus as {}", conn.getUniqueName());

            while (running) {
                if (!WAKEUP.tryAcquire(1, TimeUnit.SECONDS)) {
                    continue;
                }
                if (!running) {
                    break;
                }
                WAKEUP.drainPermits();
                refresh(conn);
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } catch (Throwable error) {
            Mcpris.LOGGER.warn("MPRIS: media integration is unavailable, the HUD will stay empty", error);
        } finally {
            connection = null;
            selectedPlayerName = null;
            currentPlaying = false;
            Song.reset();

            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException closeError) {
                    Mcpris.LOGGER.debug("MPRIS: failed to close the session bus connection", closeError);
                }
            }
        }
    }

    private static void refresh(DBusConnection conn) {
        String selectedPlayer = null;
        Map<String, Object> selectedMetadata = Map.of();
        boolean selectedPlaying = false;

        for (String player : listPlayers(conn)) {
            Map<String, Variant<?>> properties = readPlayerProperties(conn, player);
            if (properties == null) {
                continue;
            }

            boolean playing = "Playing".equals(unwrap(properties.get("PlaybackStatus")));

            if (selectedPlayer == null || (playing && !selectedPlaying)) {
                selectedPlayer = player;
                selectedMetadata = unwrapMetadata(properties.get("Metadata"));
                selectedPlaying = playing;
            }
        }

        if (selectedPlayer == null) {
            selectedPlayerName = null;
            currentPlaying = false;
            Song.reset();
        } else {
            selectedPlayerName = selectedPlayer;
            currentPlaying = selectedPlaying;

            Song.updateMetadata(selectedMetadata, selectedPlaying);
        }
    }

    public static void setPlaying(boolean playing) {
        DBusConnection conn = connection;
        String player = selectedPlayerName;

        if (conn == null || player == null) {
            Mcpris.LOGGER.debug("MPRIS: no media player selected");
            return;
        }

        try {
            PlayerControl control = conn.getRemoteObject(
                    player,
                    OBJECT_PATH,
                    PlayerControl.class,
                    false
            );

            if (playing) {
                control.Play();
            } else {
                control.Pause();
            }

            WAKEUP.release();
        } catch (Throwable error) {
            Mcpris.LOGGER.warn(
                    "MPRIS: failed to change playback state",
                    error
            );
        }
    }

    public static void togglePlayback() {
        setPlaying(!currentPlaying);
    }

    public static void stopPlayback() {
        DBusConnection conn = connection;
        String player = selectedPlayerName;

        if (conn == null || player == null) {
            Mcpris.LOGGER.debug("MPRIS: no media player selected");
            return;
        }

        try {
            PlayerControl control = conn.getRemoteObject(player, OBJECT_PATH, PlayerControl.class, false);

            control.Stop();
            WAKEUP.release();

        } catch (Throwable error) {
            Mcpris.LOGGER.warn("MPRIS: failed to stop playback", error);
        }
    }

    public static void nextTrack() {
        sendTrackCommand(true);
    }

    public static void previousTrack() {
        sendTrackCommand(false);
    }

    private static void sendTrackCommand(boolean next) {
        DBusConnection conn = connection;
        String player = selectedPlayerName;

        if (conn == null || player == null) {
            Mcpris.LOGGER.debug("MPRIS: no media player selected");
            return;
        }

        try {
            PlayerControl control = conn.getRemoteObject(player, OBJECT_PATH, PlayerControl.class, false);

            if (next) {
                control.Next();
            } else {
                control.Previous();
            }
            WAKEUP.release();

        } catch (Throwable error) {
            Mcpris.LOGGER.warn("MPRIS: failed to change track", error);
        }
    }

    private static List<String> listPlayers(DBusConnection conn) {
        try {
            DBus dbus = conn.getRemoteObject(DBUS_BUS_NAME, DBUS_OBJECT_PATH, DBus.class, false);
            List<String> players = new ArrayList<>();

            for (String name : dbus.ListNames()) {
                if (name.startsWith(BUS_NAME_PREFIX)) {
                    players.add(name);
                }
            }

            return players;

        } catch (Throwable error) {
            Mcpris.LOGGER.debug("MPRIS: could not list the bus names", error);
            return List.of();
        }
    }

    private static Map<String, Variant<?>> readPlayerProperties(DBusConnection conn, String player) {
        try {
            Properties properties = conn.getRemoteObject(player, OBJECT_PATH, Properties.class, false);
            return properties.GetAll(PLAYER_INTERFACE);
        } catch (Throwable error) {
            Mcpris.LOGGER.debug("MPRIS: could not read properties of {}", player, error);
            return null;
        }
    }

    private static void onPropertiesChanged(Properties.PropertiesChanged signal) {
        if (!PLAYER_INTERFACE.equals(signal.getInterfaceName())) {
            return;
        }

        Map<String, Variant<?>> changed = signal.getPropertiesChanged();
        if (changed.containsKey("Metadata") || changed.containsKey("PlaybackStatus")) {
            WAKEUP.release();
        }
    }

    private static void onNameOwnerChanged(DBus.NameOwnerChanged signal) {
        if (signal.name != null && signal.name.startsWith(BUS_NAME_PREFIX)) {
            WAKEUP.release();
        }
    }

    private static Object unwrap(Variant<?> variant) {
        return variant == null ? null : variant.getValue();
    }

    private static Map<String, Object> unwrapMetadata(Variant<?> metadataVariant) {
        Object value = unwrap(metadataVariant);
        if (!(value instanceof Map<?, ?> raw)) {
            return Map.of();
        }

        Map<String, Object> metadata = new HashMap<>(raw.size());
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            Object entryValue = entry.getValue();
            metadata.put(
                    String.valueOf(entry.getKey()),
                    entryValue instanceof Variant<?> variant ? variant.getValue() : entryValue );
        }

        return metadata;
    }
}