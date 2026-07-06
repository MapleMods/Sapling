package io.github.maplemods.sapling.networking.packets;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import commonnetwork.networking.data.Side;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.functions.TagFunctions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class ToServerRequestEntityTextureDataPacket implements CustomPacketPayload {
    public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "to_server_request_entity_texture_data_packet");

    public static final CustomPacketPayload.Type<ToServerRequestEntityTextureDataPacket> TYPE =
            new CustomPacketPayload.Type<>(CHANNEL);

    public static final StreamCodec<FriendlyByteBuf, ToServerRequestEntityTextureDataPacket> STREAM_CODEC =
            StreamCodec.ofMember(ToServerRequestEntityTextureDataPacket::encode, ToServerRequestEntityTextureDataPacket::new);

    private final UUID entityUUID;

    public ToServerRequestEntityTextureDataPacket(FriendlyByteBuf buf) {
        this.entityUUID = buf.readUUID();
    }

    public ToServerRequestEntityTextureDataPacket(UUID entityUUIDIn) {
        this.entityUUID = entityUUIDIn;
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
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
            ServerLevel serverLevel = serverPlayer.level();

            Entity entity = serverLevel.getEntity(uuid);
            if (entity == null) {
                return;
            }

            Identifier textureIdentifier = TagFunctions.getCustomEntityTextureIfExists(entity);
            if (textureIdentifier == null) {
                return;
            }

            Dispatcher.sendToClient(new ToClientSendEntityTextureDataPacket(uuid, textureIdentifier), serverPlayer);
        }
    }
}