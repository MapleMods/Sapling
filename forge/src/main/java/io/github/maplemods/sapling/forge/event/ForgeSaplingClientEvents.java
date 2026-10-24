package io.github.maplemods.sapling.forge.event;

import io.github.maplemods.sapling.event.SaplingClientEvents;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ForgeSaplingClientEvents {
    public static void registerEventsInBus() {
        // BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeSaplingClientEvents.class);

		ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(ForgeSaplingClientEvents::onClientLogout);
    }

	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		SaplingClientEvents.onClientLogout();
	}
}
