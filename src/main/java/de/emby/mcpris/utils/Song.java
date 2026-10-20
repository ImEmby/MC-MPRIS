package de.emby.mcpris.utils;

import com.sun.jna.platform.win32.OaIdl;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class Song {

    public record Track(String artist, String title, String album, Boolean playing) {
        public static Track empty() {
            return new Track("", "", "", false);
        }
    }

    private static final AtomicReference<Track> CURRENT =
            new AtomicReference<>(Track.empty());

    private Song() {}

    public static Track getTrack() {
        return CURRENT.get();
    }

    public static String getArtist() {
        return CURRENT.get().artist();
    }

    public static String getTitle() {
        return CURRENT.get().title();
    }

    public static String getAlbum() {
        return CURRENT.get().album();
    }

    public static Boolean getPlaying() {
        return CURRENT.get().playing();
    }

    public static void updateMetadata(Map<String, ?> metadata, Boolean playing) {
        String artist = firstArtist(metadata.get("xesam:artist"));
        String title = stringValue(metadata.get("xesam:title"));
        String album = stringValue(metadata.get("xesam:album"));

        CURRENT.set(new Track(artist, title, album, playing));
    }

    public static void reset() {
        CURRENT.set(Track.empty());
    }

    private static String stringValue(Object value) {
        return value == null ? "" : value.toString();
    }

    private static String firstArtist(Object value) {
        if (value instanceof List<?> artists) {
            return artists.isEmpty()
                    ? ""
                    : stringValue(artists.get(0));
        }

        if (value instanceof Object[] artists) {
            return artists.length == 0
                    ? ""
                    : stringValue(artists[0]);
        }

        return stringValue(value);
    }
}
