package com.skyceleste.client.features;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

public class HitColor implements FeatureRegistry.Feature {
    private boolean enabled = true;
    public int r = 255;
    public int g = 0;
    public int b = 0;

    @Override
    public String id() {
        return "hit_color";
    }

    @Override
    public String name() {
        return "Hit Color";
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
        com.skyceleste.client.utils.HitColorHelper.updateColorConfig();
    }

    @Override
    public void readConfig(JsonObject json) {
        if (json.has("r")) r = json.get("r").getAsInt();
        if (json.has("g")) g = json.get("g").getAsInt();
        if (json.has("b")) b = json.get("b").getAsInt();
        com.skyceleste.client.utils.HitColorHelper.updateColorConfig();
    }

    @Override
    public void writeConfig(JsonObject json) {
        json.addProperty("r", r);
        json.addProperty("g", g);
        json.addProperty("b", b);
    }
}
