package io.github.maplemods.sapling.networking;

import commonnetwork.api.Network;
import io.github.maplemods.sapling.networking.packets.ToClientSendEntityTextureDataPacket;
import io.github.maplemods.sapling.networking.packets.ToServerRequestEntityTextureDataPacket;

public class PacketRegistration {

	public void init() {
		initClientPackets();
		initServerPackets();
	}

	private void initClientPackets() {
		Network
                .registerPacket(ToClientSendEntityTextureDataPacket.type(), ToClientSendEntityTextureDataPacket.class, ToClientSendEntityTextureDataPacket.STREAM_CODEC, ToClientSendEntityTextureDataPacket::handle);
	}

	private void initServerPackets() {
		Network
                .registerPacket(ToServerRequestEntityTextureDataPacket.type(), ToServerRequestEntityTextureDataPacket.class, ToServerRequestEntityTextureDataPacket.STREAM_CODEC, ToServerRequestEntityTextureDataPacket::handle);
	}
}
