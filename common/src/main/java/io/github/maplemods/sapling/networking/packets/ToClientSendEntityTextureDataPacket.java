package io.github.maplemods.sapling.networking.packets;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ToClientSendEntityTextureDataPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "to_client_send_entity_texture_data_packet");
	public static final StreamCodec<FriendlyByteBuf, ToClientSendEntityTextureDataPacket> STREAM_CODEC = StreamCodec.ofMember(ToClientSendEntityTextureDataPacket::encode, ToClientSendEntityTextureDataPacket::new);

	private final UUID entityUUID;
	private final ResourceLocation textureIdentifier;

	public ToClientSendEntityTextureDataPacket(FriendlyByteBuf buf) {
		this.entityUUID = buf.readUUID();
		this.textureIdentifier = buf.readResourceLocation();
	}

	public ToClientSendEntityTextureDataPacket(UUID entityUUID, ResourceLocation textureIdentifier) {
	    this.entityUUID = entityUUID;
	    this.textureIdentifier = textureIdentifier;
	}

    public static CustomPacketPayload.Type<@NotNull CustomPacketPayload> type() {
        return new CustomPacketPayload.Type<>(CHANNEL);
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