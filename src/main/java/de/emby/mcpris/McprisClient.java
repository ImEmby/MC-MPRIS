package de.emby.mcpris;

import de.emby.mcpris.config.PositionConfigSectionScreen;
import de.emby.mcpris.mpris.MprisService;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

@Mod(value = Mcpris.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Mcpris.MODID, value = Dist.CLIENT)
public class McprisClient {
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

}
