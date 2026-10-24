package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.TextureEntry;
import io.github.maplemods.sapling.data.Textures;
import commonnetwork.api.Dispatcher;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.networking.packets.ToServerRequestEntityTextureDataPacket;
import io.github.maplemods.sapling.services.Services;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.UUID;

public class TextureFunctions {
	public static ResourceLocation getCachedEntityTexture(UUID entityUUID) {
		if (!ClientVariables.cachedTextures.containsKey(entityUUID)) {
			Dispatcher.sendToServer(new ToServerRequestEntityTextureDataPacket(entityUUID));
			return null;
		}

		return ClientVariables.cachedTextures.get(entityUUID);
	}

	public static Path getTextureConfigPath() {
		return Path.of(Services.MODLOADER.getGameDirectory()).resolve("config").resolve("maplemods").resolve("textures");
	}

	// The per-entity mixins ask this one: an engine design is drawn by the texture engine
	public static ResourceLocation getLegacyEntityTexture(UUID entityUUID) {
		ResourceLocation identifier = getCachedEntityTexture(entityUUID);
		if (identifier == null) {
			return null;
		}

		TextureEntry entry = Textures.byIdentifier.get(identifier);
		if (entry != null && entry.usesEngine()) {
			return null;
		}
		return identifier;
	}
}
