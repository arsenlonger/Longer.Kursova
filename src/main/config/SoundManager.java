package main.config;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private static float musicVolume = 0.8f;
    private static float soundEffectsVolume = 0.9f;
    private static boolean isMusicEnabled = true;
    private static boolean isSoundEffectsEnabled = true;

    private static Clip backgroundMusicClip;

    public static float getMusicVolume() { return musicVolume; }
    public static void setMusicVolume(float volume) { musicVolume = Math.max(0.0f, Math.min(1.0f, volume)); }

    public static float getSoundEffectsVolume() { return soundEffectsVolume; }
    public static void setSoundEffectsVolume(float volume) { soundEffectsVolume = Math.max(0.0f, Math.min(1.0f, volume)); }

    public static boolean isMusicEnabled() { return isMusicEnabled; }
    public static void setMusicEnabled(boolean enabled) {
        isMusicEnabled = enabled;
        if (!enabled && backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }

    public static boolean isSoundEffectsEnabled() { return isSoundEffectsEnabled; }
    public static void setSoundEffectsEnabled(boolean enabled) { isSoundEffectsEnabled = enabled; }

    public static void playSoundEffect(String filePath) {
        if (!isSoundEffectsEnabled) return;
        try {
            File soundFile = new File(filePath);
            if (soundFile.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                setClipVolume(clip, soundEffectsVolume);
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Звуковий ефект: " + e.getMessage());
        }
    }

    private static void setClipVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }
}
