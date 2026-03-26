package io.github.maplemods.sapling.neoforge.event;

import io.github.maplemods.sapling.event.SaplingClientEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public class NeoForgeSaplingClientEvents {
	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		SaplingClientEvents.onClientLogout();
	}
}
