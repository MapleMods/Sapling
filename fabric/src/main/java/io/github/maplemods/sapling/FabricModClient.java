package io.github.maplemods.sapling;

import io.github.maplemods.sapling.event.SaplingClientEvents;
import io.github.maplemods.sapling.functions.ConfigFunctions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> ConfigFunctions.initConfigAtLaunch());

		ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
			SaplingClientEvents.onClientLogout();
		});
	}
}
