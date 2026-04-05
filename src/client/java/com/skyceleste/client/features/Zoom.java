package com.skyceleste.client.features;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Zoom implements FeatureRegistry.Feature {

    private static final int MIN_ZOOM_FOV = 30;
    private static final int MAX_ZOOM_FOV = 110;
    private static final int ZOOM_STEP = 2;

    private final KeyMapping keyMapping = new KeyMapping(
            "key.skyceleste.zoom",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KeyMapping.Category.MISC);

    private boolean enabled = true;
    private boolean applied = false;
    private int savedFov = 70;
    private int zoomFov = 30;

    @Override
    public String id() {
        return "zoom";
    }

    @Override
    public String name() {
        return "Zoom";
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
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null && minecraft.options != null) {
                restoreFov(minecraft);
            } else {
                applied = false;
            }
        }
    }

    public boolean isZoomKeyDown() {
        return enabled && keyMapping.isDown();
    }

    public void adjustZoomByScroll(double scrollAmount) {
        if (!enabled || scrollAmount == 0.0) {
            return;
        }

        zoomFov = Math.max(MIN_ZOOM_FOV,
                Math.min(MAX_ZOOM_FOV, zoomFov - (int) Math.signum(scrollAmount) * ZOOM_STEP));
    }

    @Override
    public void registerKeyBindings() {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    @Override
    public void onClientTick(Minecraft minecraft) {
        if (minecraft == null || minecraft.options == null) {
            return;
        }

        if (!enabled) {
            if (applied) {
                restoreFov(minecraft);
            }
            return;
        }

        if (keyMapping.isDown()) {
            if (!applied) {
                savedFov = minecraft.options.fov().get();
                applied = true;
            }

            int clampedZoomFov = Math.max(MIN_ZOOM_FOV, Math.min(MAX_ZOOM_FOV, zoomFov));
            minecraft.options.fov().set(clampedZoomFov);
            return;
        }

        if (applied) {
            restoreFov(minecraft);
        }
    }

    @Override
    public void readConfig(JsonObject json) {
        if (json.has("zoom_fov")) {
            zoomFov = Math.max(MIN_ZOOM_FOV, Math.min(MAX_ZOOM_FOV, json.get("zoom_fov").getAsInt()));
        }
        if (json.has("zoom_key")) {
            keyMapping.setKey(InputConstants.getKey(json.get("zoom_key").getAsString()));
            KeyMapping.resetMapping();
        }
    }

    @Override
    public void writeConfig(JsonObject json) {
        json.addProperty("zoom_fov", zoomFov);
        json.addProperty("zoom_key", keyMapping.saveString());
    }

    private void restoreFov(Minecraft minecraft) {
        if (minecraft == null || minecraft.options == null) {
            applied = false;
            return;
        }
        minecraft.options.fov().set(savedFov);
        applied = false;
    }
}