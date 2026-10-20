package de.emby.mcpris.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class Sound {
    private static float savedVolume = 1.0f;

    public static void setVolume(double volume) {
        Minecraft mc = Minecraft.getInstance();

        savedVolume = mc.options.getSoundSourceVolume(SoundSource.MUSIC);
        mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set(volume);
        mc.options.save();
    }

    public static void restoreVolume() {
        Minecraft mc = Minecraft.getInstance();

        mc.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set((double) savedVolume);
        mc.options.save();
    }
}
