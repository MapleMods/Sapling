package io.github.maplemods.sapling.networking.packets;

import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.ClientVariables;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToClientSyncAllEntityTexturesPacket {
	public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "to_client_sync_all_entity_textures_packet");
	public static final StreamCodec<FriendlyByteBuf, ToClientSyncAllEntityTexturesPacket> STREAM_CODEC = StreamCodec.ofMember(ToClientSyncAllEntityTexturesPacket::encode, ToClientSyncAllEntityTexturesPacket::new);

	private final Map<UUID, Identifier> textures;

	public ToClientSyncAllEntityTexturesPacket(FriendlyByteBuf buf) {
		int size = buf.readInt();
		this.textures = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			UUID uuid = buf.readUUID();
			Identifier identifier = buf.readIdentifier();
			this.textures.put(uuid, identifier);
		}
	}

	public ToClientSyncAllEntityTexturesPacket(Map<UUID, Identifier> textures) {
		this.textures = textures;
	}

	public static CustomPacketPayload.Type<@NotNull CustomPacketPayload> type() {
		return new CustomPacketPayload.Type<>(CHANNEL);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(textures.size());
		for (Map.Entry<UUID, Identifier> entry : textures.entrySet()) {
			buf.writeUUID(entry.getKey());
			buf.writeIdentifier(entry.getValue());
		}
	}

	public static void handle(PacketContext<ToClientSyncAllEntityTexturesPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			Map<UUID, Identifier> incoming = ctx.message().textures;
			// Merge — incoming entries overwrite existing, others are kept
			ClientVariables.cachedTextures.putAll(incoming);
		}
	}
}