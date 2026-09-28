package com.alihantoleuov.mobilevibration;

import com.alihantoleuov.mobilevibration.config.VibrationConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class MobileVibrationMod implements ModInitializer {
    public static final String MOD_ID = "mobilevibration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mobilevibration.json");

    public static volatile VibrationConfig config = VibrationConfig.load(CONFIG_PATH);
    public static final HapticManager HAPTIC = new HapticManager(config);

    @Override
    public void onInitialize() {
        LOGGER.info("MobileVibration mod initializing...");
        LOGGER.info("Haptic backend available: {}", HAPTIC.isAvailable());
    }

    public static void refreshConfig() {
        config = VibrationConfig.load(CONFIG_PATH);
        HAPTIC.setConfig(config);
    }

    public static void saveConfig() {
        config.save(CONFIG_PATH);
    }
}
