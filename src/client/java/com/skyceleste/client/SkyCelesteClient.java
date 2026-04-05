package com.skyceleste.client;

import com.skyceleste.client.features.FeatureRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class SkyCelesteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FeatureRegistry.registerKeyBindings();
		FeatureRegistry.loadStates();
		ClientTickEvents.END_CLIENT_TICK.register(FeatureRegistry::tickAll);
	}
}