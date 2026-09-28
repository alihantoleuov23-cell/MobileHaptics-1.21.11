package com.alihantoleuov.mobilevibration;

import com.alihantoleuov.mobilevibration.backend.DesktopHapticBackend;
import com.alihantoleuov.mobilevibration.backend.HapticBackend;
import com.alihantoleuov.mobilevibration.backend.NativeHaptics;
import com.alihantoleuov.mobilevibration.config.VibrationConfig;
import com.alihantoleuov.mobilevibration.event.VibrationEventType;

public class HapticManager {
    private volatile VibrationConfig config;
    private final HapticBackend backend;
    private long lastTriggerTimeMs = 0L;

    public HapticManager(VibrationConfig config) {
        this.config = config;
        this.backend = isAndroidEnvironment() ? new NativeAndroidBackend() : new DesktopHapticBackend();
        MobileVibrationMod.LOGGER.info("HapticManager initialized with backend: {}", backend.getClass().getSimpleName());
    }

    public void setConfig(VibrationConfig config) {
        this.config = config;
    }

    public boolean isAvailable() {
        return backend.isAvailable();
    }

    public boolean trigger(VibrationEventType eventType) {
        if (config == null || !config.enabled || !backend.isAvailable()) {
            return false;
        }

        if (!isEventEnabled(eventType)) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (now - lastTriggerTimeMs < config.cooldown) {
            return false;
        }

        lastTriggerTimeMs = now;
        int duration = Math.max(1, Math.min(1000, config.masterDuration));
        float strength = Math.max(0.0f, Math.min(1.0f, config.masterStrength));

        backend.vibrate(duration, strength);
        return true;
    }

    private boolean isEventEnabled(VibrationEventType eventType) {
        if (config == null) {
            return false;
        }
        return switch (eventType) {
            case DAMAGE -> config.damageVibration;
            case ATTACK -> config.attackVibration;
            case CRITICAL_DAMAGE -> config.criticalVibration;
            case FALL -> config.fallVibration;
            case BLOCK_INTERACTION -> config.blockInteractionVibration;
            case ITEM_USE -> config.itemUseVibration;
            case DEATH -> config.deathVibration;
        };
    }

    private static boolean isAndroidEnvironment() {
        try {
            String osName = System.getProperty("os.name", "").toLowerCase();
            return osName.contains("android");
        } catch (Throwable e) {
            return false;
        }
    }

    private static class NativeAndroidBackend implements HapticBackend {
        @Override
        public boolean isAvailable() {
            try {
                return NativeHaptics.isAvailable();
            } catch (Throwable e) {
                return false;
            }
        }

        @Override
        public void vibrate(int duration, float strength) {
            try {
                if (duration < 1 || duration > 1000) {
                    return;
                }
                if (strength < 0.0f || strength > 1.0f) {
                    return;
                }
                NativeHaptics.vibrate((long) duration, strength);
            } catch (Throwable e) {
                MobileVibrationMod.LOGGER.debug("Failed to vibrate", e);
            }
        }
    }
}
