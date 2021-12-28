package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;

public class PingPacket extends IncomingPacket {

	public PingPacket(int packetId, byte[] data, Client origin) {
		super(packetId, 0, data, origin);
	}

	public void handlePacket() {

	}

}
