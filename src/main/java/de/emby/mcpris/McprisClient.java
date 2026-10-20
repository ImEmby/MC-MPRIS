package de.emby.mcpris;

import de.emby.mcpris.config.PositionConfigSectionScreen;
import de.emby.mcpris.mpris.MprisService;
import de.emby.mcpris.utils.Song;
import de.emby.mcpris.utils.Sound;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

@Mod(value = Mcpris.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Mcpris.MODID, value = Dist.CLIENT)
public class McprisClient {
    private static int tickCounter = 0;

    public McprisClient(ModContainer container) {
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mod, parent) -> new ConfigurationScreen(
                        container,
                        parent,
                        PositionConfigSectionScreen::new
                )
        );
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(MprisService::start);
    }

    @SubscribeEvent
    static void onGameShuttingDown(GameShuttingDownEvent event) {
        MprisService.stop();
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        if (++tickCounter >= 20) {
            tickCounter = 0;

            if (Song.getPlaying()) {
                Sound.setVolume(0);
            } else {
                Sound.setVolume(1);
            }
        }
    }
}
