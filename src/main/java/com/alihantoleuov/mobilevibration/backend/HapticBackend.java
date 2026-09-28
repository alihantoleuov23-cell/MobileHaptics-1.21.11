package com.alihantoleuov.mobilevibration.backend;

public interface HapticBackend {
    boolean isAvailable();

    void vibrate(int duration, float strength);
}
