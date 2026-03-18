package io.github.maplemods.sapling.networking.packets;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.functions.TagFunctions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class ToServerRequestEntityTextureDataPacket {
	public static final ResourceLocation CHANNEL = new ResourceLocation(Constants.MOD_ID, "to_server_request_entity_texture_data_packet");

	private final UUID entityUUID;

	public static ToServerRequestEntityTextureDataPacket decode(FriendlyByteBuf buf) {
		UUID entityUUID = buf.readUUID();
		return new ToServerRequestEntityTextureDataPacket(entityUUID);
	}

	public ToServerRequestEntityTextureDataPacket(UUID entityUUID) {
		this.entityUUID = entityUUID;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(entityUUID);
	}

	public static void handle(PacketContext<ToServerRequestEntityTextureDataPacket> ctx) {
		if (ctx.side().equals(Side.SERVER)) {
			ToServerRequestEntityTextureDataPacket packet = ctx.message();

			UUID uuid = packet.entityUUID;
			if (uuid == null) {
				return;
			}

			ServerPlayer serverPlayer = ctx.sender();
			ServerLevel serverLevel = (ServerLevel) serverPlayer.level();

			Entity entity = serverLevel.getEntity(uuid);
			if (entity == null) {
				return;
			}

			ResourceLocation textureIdentifier = TagFunctions.getCustomEntityTextureIfExists(entity);
			if (textureIdentifier == null) {
				return;
			}

			Dispatcher.sendToClient(new ToClientSendEntityTextureDataPacket(uuid, textureIdentifier), serverPlayer);
		}
	}
}