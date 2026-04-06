package com.skyceleste.client.features;

import com.google.gson.JsonObject;

public class InfiniteChat implements FeatureRegistry.Feature {

    private boolean enabled = false;

    @Override
    public String id() {
        return "infinite_chat";
    }

    @Override
    public String name() {
        return "Infinite Chat";
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
    }
}
