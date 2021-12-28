package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;

public class WindowEvent extends IncomingPacket {

	public WindowEvent(byte[] data, Client origin) {
		super(248, 1, data, origin);
	}

	public void handlePacket() {
		origin.setScreenFocus(nextUnsignedByte() == 1);
	}

}
