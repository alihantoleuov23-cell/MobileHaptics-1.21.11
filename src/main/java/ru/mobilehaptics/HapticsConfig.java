package ru.mobilehaptics;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HapticsConfig {
    public boolean enabled = true;
    public int breakMs = 25;
    public int breakAmplitude = 55;
    public int placeMs = 15;
    public int placeAmplitude = 35;

    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mobile-haptics.properties");

    public static HapticsConfig load() {
        HapticsConfig cfg = new HapticsConfig();
        if (!Files.exists(PATH)) {
            cfg.save();
            return cfg;
        }

        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(PATH)) {
            p.load(r);
            cfg.enabled = Boolean.parseBoolean(p.getProperty("enabled", Boolean.toString(cfg.enabled)));
            cfg.breakMs = clampInt(p.getProperty("break_ms"), cfg.breakMs, 1, 2000);
            cfg.breakAmplitude = clampInt(p.getProperty("break_amplitude"), cfg.breakAmplitude, 1, 255);
            cfg.placeMs = clampInt(p.getProperty("place_ms"), cfg.placeMs, 1, 2000);
            cfg.placeAmplitude = clampInt(p.getProperty("place_amplitude"), cfg.placeAmplitude, 1, 255);
        } catch (IOException ignored) {
        }
        return cfg;
    }

    public void save() {
        Properties p = new Properties();
        p.setProperty("enabled", Boolean.toString(enabled));
        p.setProperty("break_ms", Integer.toString(breakMs));
        p.setProperty("break_amplitude", Integer.toString(breakAmplitude));
        p.setProperty("place_ms", Integer.toString(placeMs));
        p.setProperty("place_amplitude", Integer.toString(placeAmplitude));
        try (Writer w = Files.newBufferedWriter(PATH)) {
            p.store(w, "Mobile Haptics settings");
        } catch (IOException ignored) {
        }
    }

    private static int clampInt(String value, int fallback, int min, int max) {
        if (value == null) return fallback;
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(value)));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}