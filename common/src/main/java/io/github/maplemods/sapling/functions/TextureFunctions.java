package io.github.maplemods.sapling.functions;

import commonnetwork.api.Dispatcher;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.networking.packets.ToServerRequestEntityTextureDataPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class TextureFunctions {
	public static ResourceLocation getCachedEntityTexture(UUID entityUUID) {
		if (!ClientVariables.cachedTextures.containsKey(entityUUID)) {
			Dispatcher.sendToServer(new ToServerRequestEntityTextureDataPacket(entityUUID));
			return null;
		}

		return ClientVariables.cachedTextures.get(entityUUID);
	}
}
