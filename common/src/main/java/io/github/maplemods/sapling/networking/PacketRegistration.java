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
				.registerPacket(ToClientSendEntityTextureDataPacket.CHANNEL, ToClientSendEntityTextureDataPacket.class, ToClientSendEntityTextureDataPacket::encode, ToClientSendEntityTextureDataPacket::decode, ToClientSendEntityTextureDataPacket::handle)
				.registerPacket(ToClientSyncAllEntityTexturesPacket.CHANNEL, ToClientSyncAllEntityTexturesPacket.class, ToClientSyncAllEntityTexturesPacket::encode, ToClientSyncAllEntityTexturesPacket::decode, ToClientSyncAllEntityTexturesPacket::handle);
	}

	private void initServerPackets() {
		Network
				.registerPacket(ToServerRequestEntityTextureDataPacket.CHANNEL, ToServerRequestEntityTextureDataPacket.class, ToServerRequestEntityTextureDataPacket::encode, ToServerRequestEntityTextureDataPacket::decode, ToServerRequestEntityTextureDataPacket::handle);
	}
}