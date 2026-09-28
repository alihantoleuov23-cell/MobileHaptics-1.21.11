package com.alihantoleuov.mobilevibration.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class VibrationConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public float masterStrength = 1.0f;
    public int masterDuration = 30;

    public boolean damageVibration = true;
    public boolean attackVibration = true;
    public boolean criticalVibration = true;
    public boolean fallVibration = true;
    public boolean blockInteractionVibration = false;
    public boolean itemUseVibration = false;
    public boolean deathVibration = true;

    public int cooldown = 40;

    public static VibrationConfig defaults() {
        return new VibrationConfig();
    }

    public static VibrationConfig load(Path path) {
        VibrationConfig config = defaults();
        if (path == null) {
            return config;
        }

        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                String text = Files.readString(path, StandardCharsets.UTF_8);
                if (!text.isBlank()) {
                    VibrationConfig loaded = GSON.fromJson(text, VibrationConfig.class);
                    if (loaded != null) {
                        config = loaded;
                    }
                }
            }
            config.validate();
            config.save(path);
            return config;
        } catch (JsonSyntaxException | IOException e) {
            config = defaults();
            try {
                config.save(path);
            } catch (IOException ignored) {
            }
            return config;
        }
    }

    public void save(Path path) {
        try {
            if (path == null) {
                return;
            }
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public void validate() {
        enabled = Boolean.TRUE.equals(enabled);
        masterStrength = clampFloat(masterStrength, 0.0f, 1.0f);
        masterDuration = clampInt(masterDuration, 1, 1000);

        damageVibration = Boolean.TRUE.equals(damageVibration);
        attackVibration = Boolean.TRUE.equals(attackVibration);
        criticalVibration = Boolean.TRUE.equals(criticalVibration);
        fallVibration = Boolean.TRUE.equals(fallVibration);
        blockInteractionVibration = Boolean.TRUE.equals(blockInteractionVibration);
        itemUseVibration = Boolean.TRUE.equals(itemUseVibration);
        deathVibration = Boolean.TRUE.equals(deathVibration);
        cooldown = clampInt(cooldown, 0, 5000);
    }

    private static float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
