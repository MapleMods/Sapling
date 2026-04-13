package io.github.maplemods.sapling.functions;

import commonnetwork.api.Dispatcher;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.networking.packets.ToServerRequestEntityTextureDataPacket;
import io.github.maplemods.sapling.services.Services;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;
import java.util.UUID;

public class TextureFunctions {
	public static Identifier getCachedEntityTexture(UUID entityUUID) {
		if (!ClientVariables.cachedTextures.containsKey(entityUUID)) {
			Dispatcher.sendToServer(new ToServerRequestEntityTextureDataPacket(entityUUID));
			return null;
		}

		return ClientVariables.cachedTextures.get(entityUUID);
	}

	public static Path getTextureConfigPath() {
		return Path.of(Services.MODLOADER.getGameDirectory()).resolve("config").resolve("maplemods").resolve("textures");
	}
}
