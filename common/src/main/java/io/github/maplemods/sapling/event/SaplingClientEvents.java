package io.github.maplemods.sapling.event;

import io.github.maplemods.sapling.data.ClientVariables;

public class SaplingClientEvents {
	public static void onClientLogout() {
		ClientVariables.cachedTextures.clear();
	}
}
