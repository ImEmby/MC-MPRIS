package de.emby.mcpris.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue position = BUILDER
            .comment(" Position of the mcpris display")
            .comment(" 0 = top left, 1 = top right, 2 = bottom left, 3 = bottom right")
            .defineInRange("position", 0, 0, 3);

    public static final ModConfigSpec.IntValue margin = BUILDER
            .comment(" Margin to the screen edge")
            .defineInRange("margin", 10, 0, 200);

    public static final ModConfigSpec.IntValue width = BUILDER
            .comment(" Width of the module")
            .defineInRange("width", 160, 0, 200);

    public static final ModConfigSpec.IntValue height = BUILDER
            .comment(" Height of the module")
            .defineInRange("height", 35, 0, 200);

    public static final ModConfigSpec.IntValue line_width = BUILDER
            .comment(" Width of the line on the side of the module")
            .defineInRange("line_width", 3, 0, 200);

    public static final ModConfigSpec.IntValue line_position = BUILDER
            .comment(" Position of the line")
            .comment(" 0 = left, 1 = right")
            .defineInRange("line_position", 0, 0, 1);

    public static final ModConfigSpec.ConfigValue<String> bg_color = BUILDER
            .comment(" Background color of the module")
            .comment(" Default: #1E1E2EE0")
            .define("bg_color", "#1E1E2EE0");

    public static final ModConfigSpec.ConfigValue<String> line_color = BUILDER
            .comment(" Color of the line")
            .comment(" Default: #B4BEFE")
            .define("line_color", "#B4BEFE");

    public static final ModConfigSpec.ConfigValue<String> text_color = BUILDER
            .comment(" Color of the top text (title)")
            .comment(" Default: #CDD6F4")
            .define("text_color", "#CDD6F4");

    public static final ModConfigSpec.ConfigValue<String> subtext_color = BUILDER
            .comment(" Color of the bottom text (artist)")
            .comment(" Default: #BAC2DE")
            .define("subtext_color", "#BAC2DE");

    public static final ModConfigSpec.IntValue title_size = BUILDER
            .comment(" Size of the top line (title)")
            .defineInRange("title_size", 100, 0, 200);

    public static final ModConfigSpec.IntValue artist_size = BUILDER
            .comment(" Size of the bottom line (artist)")
            .defineInRange("artist_size", 75, 0, 200);

    public static final ModConfigSpec.IntValue line_spacing = BUILDER
            .comment(" Spacing between lines")
            .defineInRange("line_spacing", 15, 0, 200);

    public static final ModConfigSpec.ConfigValue<String> elide = BUILDER
            .comment(" String used to elide title and artist")
            .comment(" Default: …")
            .define("elide", "…");

    public static final ModConfigSpec.BooleanValue use_dynamic_width = BUILDER
            .comment(" Whether to use dynamic width for the module")
            .comment(" Default: true")
            .define("use_dynamic_width", true);

    public static final ModConfigSpec.BooleanValue use_dynamic_height = BUILDER
            .comment(" Whether to use dynamic height for the module")
            .comment(" Default: true")
            .define("use_dynamic_height", true);

    public static final ModConfigSpec.BooleanValue use_dynamic_line_spacing = BUILDER
            .comment(" Whether to use dynamic line spacing")
            .comment(" Default: true")
            .define("use_dynamic_line_spacing", true);

    public static final ModConfigSpec.DoubleValue polling_rate = BUILDER
            .comment(" Polling rate for the in-game music muting in seconds")
            .defineInRange("polling_rate", 3.0, 0.0, 30.0);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
