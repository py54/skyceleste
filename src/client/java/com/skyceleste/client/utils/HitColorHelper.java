package com.skyceleste.client.utils;

import com.skyceleste.client.features.FeatureRegistry;

public class HitColorHelper {
    public static Runnable ON_COLOR_UPDATE = () -> {};

    public static void updateColorConfig() {
        ON_COLOR_UPDATE.run();
    }
    
    public static int getRed() {
        return FeatureRegistry.HIT_COLOR.isEnabled() ? FeatureRegistry.HIT_COLOR.r : 255;
    }

    public static int getGreen() {
        return FeatureRegistry.HIT_COLOR.isEnabled() ? FeatureRegistry.HIT_COLOR.g : 0;
    }

    public static int getBlue() {
        return FeatureRegistry.HIT_COLOR.isEnabled() ? FeatureRegistry.HIT_COLOR.b : 0;
    }
}
