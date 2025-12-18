package io.github.maplemods.sapling.networking.packets;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.functions.TagFunctions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class ToServerRequestEntityTextureDataPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "to_server_request_entity_texture_data_packet");
	public static final StreamCodec<FriendlyByteBuf, ToServerRequestEntityTextureDataPacket> STREAM_CODEC = StreamCodec.ofMember(ToServerRequestEntityTextureDataPacket::encode, ToServerRequestEntityTextureDataPacket::new);

	private final UUID entityUUID;

	public ToServerRequestEntityTextureDataPacket(FriendlyByteBuf buf) {
		this.entityUUID = buf.readUUID();
	}

	public ToServerRequestEntityTextureDataPacket(UUID entityUUIDIn) {
		this.entityUUID = entityUUIDIn;
	}

    public static CustomPacketPayload.Type<CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
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
			ServerLevel serverLevel = serverPlayer.serverLevel();

			Entity entity = serverLevel.getEntity(uuid);
			if (entity == null) {
				return;
			}

			ResourceLocation textureResourceLocation = TagFunctions.getCustomEntityTextureIfExists(entity);
			if (textureResourceLocation == null) {
				return;
			}

			Dispatcher.sendToClient(new ToClientSendEntityTextureDataPacket(uuid, textureResourceLocation), serverPlayer);
		}
	}
}