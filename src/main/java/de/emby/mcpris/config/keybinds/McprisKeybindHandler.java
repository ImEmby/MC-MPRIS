package de.emby.mcpris.config.keybinds;

import de.emby.mcpris.Mcpris;
import de.emby.mcpris.McprisHud;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(
        modid = Mcpris.MODID,
        value = Dist.CLIENT
)
public class McprisKeybindHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().screen != null) {
            return;
        }

        while (McprisKeybinds.TOGGLE_HUD.consumeClick()) {
            McprisHud.visible = !McprisHud.visible;
        }
    }


    @SubscribeEvent
    public static void onScreenKey(ScreenEvent.KeyPressed.Pre event) {
        var pressedKey = com.mojang.blaze3d.platform.InputConstants.getKey(
                event.getKeyCode(),
                event.getScanCode()
        );

        if (McprisKeybinds.TOGGLE_HUD.isActiveAndMatches(pressedKey)) {
            McprisHud.visible = !McprisHud.visible;
            event.setCanceled(true);
        }
    }
}
