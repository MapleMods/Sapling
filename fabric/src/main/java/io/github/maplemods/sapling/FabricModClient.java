package io.github.maplemods.sapling;

import io.github.maplemods.sapling.event.SaplingClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class FabricModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
			SaplingClientEvents.onClientLogout();
		});
	}
}
