package de.emby.mcpris.config.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import de.emby.mcpris.Mcpris;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
        modid = Mcpris.MODID,
        value = Dist.CLIENT
)
public class McprisKeybinds {

    public static final KeyMapping TOGGLE_HUD =
            new KeyMapping(
                    "key.mcpris.toggle_hud",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    "key.categories.mcpris"
            );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_HUD);
    }
}