package com.alihantoleuov.mobilevibration.backend;

public class DesktopHapticBackend implements HapticBackend {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void vibrate(int duration, float strength) {
    }
}
