package org.mightykill.rsps.io.packets.outgoing;

public class DisplayInterface extends OutgoingPacket {

	public DisplayInterface(int interfaceId, int childId, int show, int window) {
		super(93, 7);
		addShort(childId);
		addByte((byte)show);
		addShort(window);
		addShort(interfaceId);
	}

}
