package com.alihantoleuov.mobilevibration.backend;

public final class NativeHaptics {
    static {
        try {
            System.loadLibrary("mobilevibration");
        } catch (Throwable ignored) {
        }
    }

    private NativeHaptics() {
    }

    public static native boolean isAvailable();

    public static native void vibrate(long duration, float strength);
}
