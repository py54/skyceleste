package com.skyceleste.client.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FeatureRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("skyceleste-features.json");

    public static final Zoom ZOOM = new Zoom();
    public static final FullBright FULLBRIGHT = new FullBright();
    public static final ScrollableTooltips SCROLLABLE_TOOLTIPS = new ScrollableTooltips();

    public static final List<Feature> FEATURES = List.<Feature>of(
            ZOOM,
            FULLBRIGHT,
            SCROLLABLE_TOOLTIPS);

    public static void registerKeyBindings() {
        for (Feature feature : FEATURES) {
            feature.registerKeyBindings();
        }
    }

    public static void tickAll(Minecraft minecraft) {
        for (Feature feature : FEATURES) {
            feature.onClientTick(minecraft);
        }
    }

    public static void loadStates() {
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }

        try {
            String raw = Files.readString(CONFIG_PATH);
            JsonObject root = GSON.fromJson(raw, JsonObject.class);
            if (root == null) {
                return;
            }

            for (Feature feature : FEATURES) {
                if (!root.has(feature.id())) {
                    continue;
                }

                if (root.get(feature.id()).isJsonPrimitive()) {
                    feature.setEnabled(root.get(feature.id()).getAsBoolean());
                    continue;
                }

                if (!root.get(feature.id()).isJsonObject()) {
                    continue;
                }

                JsonObject featureJson = root.getAsJsonObject(feature.id());
                if (featureJson.has("enabled")) {
                    feature.setEnabled(featureJson.get("enabled").getAsBoolean());
                }
                feature.readConfig(featureJson);
            }
        } catch (IOException e) {
            System.err.println("[SkyCeleste] Failed to load feature config: " + e.getMessage());
        }
    }

    public static void saveStates() {
        JsonObject root = new JsonObject();
        for (Feature feature : FEATURES) {
            JsonObject featureJson = new JsonObject();
            featureJson.addProperty("enabled", feature.isEnabled());
            feature.writeConfig(featureJson);
            root.add(feature.id(), featureJson);
        }

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException e) {
            System.err.println("[SkyCeleste] Failed to save feature config: " + e.getMessage());
        }
    }

    public interface Feature {
        String id();

        String name();

        String category();

        boolean isEnabled();

        void setEnabled(boolean enabled);

        default void toggle() {
            setEnabled(!isEnabled());
        }

        default void onClientTick(Minecraft minecraft) {
        }

        default void registerKeyBindings() {
        }

        default void readConfig(JsonObject json) {
        }

        default void writeConfig(JsonObject json) {
        }
    }
}