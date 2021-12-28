package org.mightykill.rsps.io.packets.outgoing;

public class DestroyGroundItem extends OutgoingPacket {

	public DestroyGroundItem(int itemId) {
		super(201, 3);
		
		addByte(0);
		addShort(itemId);
	}

}
