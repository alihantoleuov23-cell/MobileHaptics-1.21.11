package com.alihantoleuov.mobilevibration;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobileVibrationMod implements ModInitializer {
    public static final String MOD_ID = "mobilevibration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing MobileVibration mod...");
        
        // Initialize configuration
        VibrationConfig.load();
        LOGGER.info("Configuration loaded");
        
        // Initialize haptic manager
        HapticManager.initialize();
        LOGGER.info("Haptic system initialized. Backend available: {}", HapticManager.isAvailable());
        
        // Register event listeners
        VibrationEventListener.register();
        LOGGER.info("Event listeners registered");
        
        LOGGER.info("MobileVibration mod initialized successfully");
    }
}
