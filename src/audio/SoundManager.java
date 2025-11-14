package audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundManager {
    private static final Map<String, Clip> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean muted = false;  // global state of sound
    private static volatile String currentLooping = null;

    public static void play(String resourcePath) {
        if (muted) return;  // no sound played
        try {
            Clip c = CACHE.computeIfAbsent(resourcePath, SoundManager::loadClip);
            if (c == null) return;
            if (c.isRunning()) c.stop();
            c.setFramePosition(0);
            c.start();
        } catch (Exception e) {
            System.err.println("[Sound] Play failed: " + resourcePath + " -> " + e.getMessage());
        }
    }

    private static Clip loadClip(String path) {
        String p = path.startsWith("/") ? path : "/" + path;
        try (InputStream raw = SoundManager.class.getResourceAsStream(p)) {
            if (raw == null) throw new IllegalArgumentException("Resource not found: " + p);
            try (BufferedInputStream in = new BufferedInputStream(raw);
                 AudioInputStream ais = AudioSystem.getAudioInputStream(in)) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                return clip;
            }
        } catch (Exception e) {
            System.err.println("[Sound] Load failed: " + p + " -> " + e);
            return null;
        }
    }


    public static void playLoop(String resourcePath) {
        if (muted) return;  // no sound played
        try {
            Clip c = CACHE.computeIfAbsent(resourcePath, SoundManager::loadClip);
            if (c == null) return;
            stopAll();
            c.setFramePosition(0);
            c.loop(Clip.LOOP_CONTINUOUSLY);
            c.start();
            currentLooping = resourcePath;  // useful for unmute
        } catch (Exception e) {
            System.err.println("[Sound] Loop failed: " + resourcePath + " -> " + e.getMessage());
        }
    }

    public static void cutAllSound() {
        muted = true;
        stopAll();
        System.out.println("[Sound] Global sound muted.");
    }

    public static void uncutAllSound() {
        muted = false;
        System.out.println("[Sound] Global sound unmuted");
        System.out.println("[Sound] current looping : " + currentLooping);
        if (currentLooping != null) {  // when unmute
            playLoop(currentLooping);
        }
    }

    public static void stop(String resourcePath) {
        try {
            Clip c = CACHE.get(resourcePath);
            if (c != null && c.isRunning()) {
                c.stop();
                c.setFramePosition(0);
            }
        } catch (Exception e) {
            System.err.println("[Sound] Stop failed: " + resourcePath + " -> " + e.getMessage());
        }
    }

    public static void stopAll() {
        for (Clip c : CACHE.values()) {
            if (c.isRunning()) c.stop();
        }
    }
}
