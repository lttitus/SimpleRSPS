package org.mightykill.rsps.io.packets.outgoing;

import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public class SendItems extends OutgoingPacket {

	public SendItems(int interfaceId, int childId, int type, Container c) {
		super(255, 0, true, true);
		Item[] items = c.getItems();
		addShort(interfaceId);
		addShort(childId);
		addShort(type);
		addShort(items.length);
		for(Item item:items) {
			if(item != null) {
				int amount = item.getItemAmount();
				if(amount > 254) {
					addByteS(255);
					addInt_v2(amount);
				}else {
					addByteS(amount);
				}
				addShortBigEndian(item.getItemId()+1);
			}else {
				addByteS(0);
				addShort(0);
			}
		}
	}

}
