package io.github.maplemods.sapling.networking;

import commonnetwork.api.Network;
import io.github.maplemods.sapling.networking.packets.ToClientSendEntityTextureDataPacket;
import io.github.maplemods.sapling.networking.packets.ToClientSyncAllEntityTexturesPacket;
import io.github.maplemods.sapling.networking.packets.ToServerRequestEntityTextureDataPacket;

public class PacketRegistration {

    public void init() {
        initClientPackets();
        initServerPackets();
    }

    private void initClientPackets() {
        Network
                .registerPacket(ToClientSendEntityTextureDataPacket.TYPE, ToClientSendEntityTextureDataPacket.STREAM_CODEC, ToClientSendEntityTextureDataPacket::handle)
                .registerPacket(ToClientSyncAllEntityTexturesPacket.TYPE, ToClientSyncAllEntityTexturesPacket.STREAM_CODEC, ToClientSyncAllEntityTexturesPacket::handle);
    }

    private void initServerPackets() {
        Network
                .registerPacket(ToServerRequestEntityTextureDataPacket.TYPE, ToServerRequestEntityTextureDataPacket.STREAM_CODEC, ToServerRequestEntityTextureDataPacket::handle);
    }
}