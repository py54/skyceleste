package com.skyceleste.client.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import java.lang.reflect.Field;

public class FullBright implements FeatureRegistry.Feature {

    private boolean enabled = false;
    private double savedGamma = 0.5;
    private double savedDarknessScale = 1.0;

    private static final double TARGET_GAMMA = 15.0;   // keep this reasonable
    private static final double TARGET_DARKNESS_SCALE = 0.0;

    private Field gammaField;
    private Field darknessScaleField;

    @Override
    public String id() {
        return "fullbright";
    }

    @Override
    public String name() {
        return "FullBright";
    }

    @Override
    public String category() {
        return "Quality Of Life";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            restoreSettings(Minecraft.getInstance());
        }
    }

    @Override
    public void onClientTick(Minecraft minecraft) {
        if (minecraft == null || minecraft.options == null) {
            return;
        }

        if (enabled) {
            if (gammaField == null) {
                initFields(minecraft);
            }

            if (!applied) {
                savedGamma = minecraft.options.gamma().get();
                savedDarknessScale = minecraft.options.darknessEffectScale().get();
                applied = true;
            }

            setGammaDirect(TARGET_GAMMA);
            minecraft.options.darknessEffectScale().set(TARGET_DARKNESS_SCALE);
            return;
        }

        if (applied) {
            restoreSettings(minecraft);
        }
    }

    private void initFields(Minecraft minecraft) {
        try {
            gammaField = OptionInstance.class.getDeclaredField("value");
            gammaField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGammaDirect(double value) {
        try {
            if (gammaField != null) {
                gammaField.set(Minecraft.getInstance().options.gamma(), value);
            }
        } catch (Exception ignored) {}
    }

    private boolean applied = false;

    private void restoreSettings(Minecraft minecraft) {
        if (minecraft == null || minecraft.options == null) {
            applied = false;
            return;
        }

        minecraft.options.gamma().set(savedGamma);
        minecraft.options.darknessEffectScale().set(savedDarknessScale);
        applied = false;
    }
}