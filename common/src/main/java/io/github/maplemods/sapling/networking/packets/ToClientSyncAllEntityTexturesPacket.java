package io.github.maplemods.sapling.networking.packets;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToClientSyncAllEntityTexturesPacket {
	public static final ResourceLocation CHANNEL = new ResourceLocation(Constants.MOD_ID, "to_client_sync_all_entity_textures_packet");

	private final Map<UUID, ResourceLocation> textures;

	public static ToClientSyncAllEntityTexturesPacket decode(FriendlyByteBuf buf) {
		int size = buf.readInt();
		Map<UUID, ResourceLocation> textures = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			UUID uuid = buf.readUUID();
			ResourceLocation identifier = buf.readResourceLocation();
			textures.put(uuid, identifier);
		}
		return new ToClientSyncAllEntityTexturesPacket(textures);
	}

	public ToClientSyncAllEntityTexturesPacket(Map<UUID, ResourceLocation> textures) {
		this.textures = textures;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(textures.size());
		for (Map.Entry<UUID, ResourceLocation> entry : textures.entrySet()) {
			buf.writeUUID(entry.getKey());
			buf.writeResourceLocation(entry.getValue());
		}
	}

	public static void handle(PacketContext<ToClientSyncAllEntityTexturesPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			Map<UUID, ResourceLocation> incoming = ctx.message().textures;
			ClientVariables.cachedTextures.putAll(incoming);
		}
	}
}