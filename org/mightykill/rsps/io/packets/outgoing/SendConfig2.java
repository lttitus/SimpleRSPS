package org.mightykill.rsps.io.packets.outgoing;

public class SendConfig2 extends OutgoingPacket {
	
	public SendConfig2(int configId, int set) {
		super(161, 3);
		addShort(configId);
		addInt_v1(set);
	}

}
