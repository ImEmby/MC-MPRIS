package de.emby.mcpris.utils;

public class Color {
    public static int hexToArgb(String hex, String fallback) {
        fallback = Text.strip(fallback, "#");
        hex = Text.strip(hex, "#");

        if (hex.matches("[0-9a-fA-F]{6}")) {
            hex = hex + "FF";
        }

        if (!hex.matches("[0-9a-fA-F]{8}")) {
            hex = fallback;
        }

        long rgba = Long.parseLong(hex, 16);
        return (int) (((rgba & 0xFF) << 24) | (rgba >>> 8));
    }
}
