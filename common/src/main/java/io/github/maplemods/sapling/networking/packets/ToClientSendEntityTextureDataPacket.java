package io.github.maplemods.sapling.networking.packets;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class ToClientSendEntityTextureDataPacket {
	public static final ResourceLocation CHANNEL = new ResourceLocation(Constants.MOD_ID, "to_client_send_entity_texture_data_packet");

	private final UUID entityUUID;
	private final ResourceLocation textureIdentifier;

	public static ToClientSendEntityTextureDataPacket decode(FriendlyByteBuf buf) {
		UUID entityUUID = buf.readUUID();
		ResourceLocation textureIdentifier = buf.readResourceLocation();
		return new ToClientSendEntityTextureDataPacket(entityUUID, textureIdentifier);
	}

	public ToClientSendEntityTextureDataPacket(UUID entityUUID, ResourceLocation textureIdentifier) {
		this.entityUUID = entityUUID;
		this.textureIdentifier = textureIdentifier;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(entityUUID);
		buf.writeResourceLocation(textureIdentifier);
	}

	public static void handle(PacketContext<ToClientSendEntityTextureDataPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSendEntityTextureDataPacket packet = ctx.message();

			UUID entityUUID = packet.entityUUID;
			ResourceLocation textureIdentifier = packet.textureIdentifier;

			if (entityUUID == null || textureIdentifier == null) {
				return;
			}

			ClientVariables.cachedTextures.put(entityUUID, textureIdentifier);
		}
	}
}