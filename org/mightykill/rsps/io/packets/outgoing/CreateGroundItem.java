package org.mightykill.rsps.io.packets.outgoing;

import org.mightykill.rsps.items.Item;

public class CreateGroundItem extends OutgoingPacket {

	public CreateGroundItem(int itemId, int itemAmount) {
		super(25, 5);
		addShortBigEndianA(itemAmount);
		addByte(0);
		addShortBigEndianA(itemId);
	}
	
	public CreateGroundItem(Item item) {
		this(item.getItemId(), item.getItemAmount());
	}

}
