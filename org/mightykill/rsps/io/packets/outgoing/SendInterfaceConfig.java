package org.mightykill.rsps.io.packets.outgoing;

public class SendInterfaceConfig extends OutgoingPacket {

	public SendInterfaceConfig(int interfaceId, int childId, int set) {
		super(59, 5);
		addByteC(set);
		addShort(childId);
		addShort(interfaceId);
	}

}
