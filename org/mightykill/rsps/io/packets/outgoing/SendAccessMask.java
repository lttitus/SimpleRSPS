package org.mightykill.rsps.io.packets.outgoing;

public class SendAccessMask extends OutgoingPacket {

	public SendAccessMask(int set, int window, int interfaceId, int offset, int length) {
		super(223, 12);
		
		addShort(length);
		addShortBigEndianA(offset);
		addShortBigEndian(window);
		addShortBigEndian(interfaceId);
		addShortBigEndian(set);
		addShortBigEndian(0);
	}

}
