package de.emby.mcpris.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PositionConfigSectionScreen
        extends ConfigurationScreen.ConfigurationSectionScreen {

    private static final String[] POSITIONS = {
            "mcpris.configuration.position.top_left",
            "mcpris.configuration.position.top_right",
            "mcpris.configuration.position.bottom_left",
            "mcpris.configuration.position.bottom_right"
    };

    private static final String[] LINE_POSITIONS = {
            "mcpris.configuration.line_position.left",
            "mcpris.configuration.line_position.right"
    };

    public PositionConfigSectionScreen(
            Screen parent,
            ModConfig.Type type,
            ModConfig config,
            Component title
    ) {
        super(parent, type, config, title);
    }

    @Override
    protected Element createIntegerValue(
            String key,
            net.neoforged.neoforge.common.ModConfigSpec.ValueSpec spec,
            Supplier<Integer> source,
            Consumer<Integer> target
    ) {
        String[] options;
        String label;

        if (key.equals("position")) {
            options = POSITIONS;
            label = "mcpris.configuration.label_position";
        } else if (key.equals("line_position")) {
            options = LINE_POSITIONS;
            label = "mcpris.configuration.label_line_position";
        } else {
            return super.createIntegerValue(key, spec, source, target);
        }

        int current = Math.floorMod(source.get(), options.length);

        Button button = Button.builder(
                Component.translatable(label, Component.translatable(options[current])),
                b -> {
                    int next = (Math.floorMod(source.get(), options.length) + 1)
                            % options.length;

                    target.accept(next);
                    onChanged(key);

                    b.setMessage(
                            Component.translatable(
                                    label,
                                    Component.translatable(options[next])
                            )
                    );
                }
        ).bounds(0, 0, 150, 20).build();

        return new Element(
                getTranslationComponent(key),
                getTooltipComponent(key, null),
                button,
                true
        );
    }
}
