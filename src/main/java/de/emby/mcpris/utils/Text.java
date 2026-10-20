package de.emby.mcpris.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class Text {
    public static String strip(String text, String chr) {
        return text.startsWith(chr) ? text.substring(1) : text;
    }

    public static int getTextWidth(String text) {
        Font font = Minecraft.getInstance().font;
        return font.width(text);
    }

    public static int getTextHeight() {
        Font font = Minecraft.getInstance().font;
        return font.lineHeight;
    }

    public static String truncate(String text, int maxWidth, String elide) {
        Font font = Minecraft.getInstance().font;

        if (text == null) {
            return "";
        }

        if (text.isEmpty() || font.width(text) <= maxWidth) {
            return text;
        }

        String suffix = elide == null ? "" : elide;
        int budget = maxWidth - font.width(suffix);

        if (budget <= 0) {
            return font.width(suffix) <= maxWidth ? suffix : "";
        }

        return font.plainSubstrByWidth(text, budget) + suffix;
    }
}
