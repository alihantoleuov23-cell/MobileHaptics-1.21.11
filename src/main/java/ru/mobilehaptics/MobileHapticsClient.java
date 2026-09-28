package ru.mobilehaptics;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class MobileHapticsClient implements ClientModInitializer {
    public static final HapticsConfig CONFIG = HapticsConfig.load();
    public static final KeyBinding OPEN_CONFIG = new KeyBinding(
            "key.mobilehaptics.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.mobilehaptics"
    );

    @Override
    public void onInitializeClient() {
        NativeVibrator.init();
        KeyBindingHelper.registerKeyBinding(OPEN_CONFIG);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_CONFIG.wasPressed()) {
                client.setScreen(new MobileHapticsScreen(client.currentScreen));
            }
        });
    }

    public static void vibrateBreak() {
        if (CONFIG.enabled) NativeVibrator.vibrate(CONFIG.breakMs, CONFIG.breakAmplitude);
    }

    public static void vibratePlace() {
        if (CONFIG.enabled) NativeVibrator.vibrate(CONFIG.placeMs, CONFIG.placeAmplitude);
    }
}