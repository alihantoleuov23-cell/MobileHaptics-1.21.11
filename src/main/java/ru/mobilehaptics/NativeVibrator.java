package ru.mobilehaptics;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minimal Android JNI bridge using the Application handle exported by Zalith.
 * The native library calls android.os.Vibrator from the Minecraft JVM process.
 */
public final class NativeVibrator {
    private static final String LIB_NAME = "mobilehaptics_bridge";
    private static boolean loaded;

    private NativeVibrator() {}

    public static void init() {
        if (loaded) return;
        if (System.getenv("DALVIK_APPLICATION") == null) return;

        String abi = System.getProperty("os.arch", "").toLowerCase();
        String arch;
        if (abi.contains("aarch64") || abi.contains("arm64")) {
            arch = "arm64-v8a";
        } else if (abi.contains("x86_64") || abi.contains("amd64")) {
            arch = "x86_64";
        } else {
            return;
        }

        Path out = FabricLoader.getInstance().getConfigDir().resolve(LIB_NAME + ".so");
        String resource = "/native/" + arch + "/lib" + LIB_NAME + ".so";
        try (InputStream in = NativeVibrator.class.getResourceAsStream(resource)) {
            if (in == null) return;
            Files.createDirectories(out.getParent());
            try (OutputStream os = Files.newOutputStream(out)) {
                in.transferTo(os);
            }
            System.load(out.toAbsolutePath().toString());
            loaded = true;
        } catch (IOException | UnsatisfiedLinkError ignored) {
            loaded = false;
        }
    }

    public static boolean vibrate(int durationMs, int amplitude) {
        if (!loaded) init();
        return loaded && nativeVibrate(durationMs, amplitude);
    }

    private static native boolean nativeVibrate(int durationMs, int amplitude);
}