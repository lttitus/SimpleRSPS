package org.mightykill.rsps.io.packets.outgoing;

public class SendConfig extends OutgoingPacket {

	public SendConfig(int configId, int set) {
		super(100, 3);
		addShortA(configId);
		addByteA(set);
	}

}
