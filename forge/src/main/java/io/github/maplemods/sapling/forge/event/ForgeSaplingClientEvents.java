package io.github.maplemods.sapling.forge.event;

import io.github.maplemods.sapling.event.SaplingClientEvents;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSaplingClientEvents {
	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		SaplingClientEvents.onClientLogout();
	}
}
