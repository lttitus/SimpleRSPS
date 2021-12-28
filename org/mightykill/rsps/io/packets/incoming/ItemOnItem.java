package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Inventory;
import org.mightykill.rsps.items.useresults.ItemOnItemResult;

public class ItemOnItem extends IncomingPacket {
	
	private Player p;
	
	private int usedWith, usedItem;
	private int usedSlot, withSlot;

	public ItemOnItem(byte[] data, Player origin) {
		super(40, 8, data, origin.getClient());
		this.p = origin;
		
		usedWith = nextUnsignedShortBigEndian();
		usedItem = nextUnsignedShortA();
		usedSlot = nextUnsignedShortA();
		withSlot = nextUnsignedShortA();
	}

	public void handlePacket() {
		if(usedSlot < 0 ||
				withSlot < 0 ||
				usedSlot >= Inventory.MAX_SIZE ||
				withSlot >= Inventory.MAX_SIZE) {
			System.err.println(p.getName()+" sent an invalid Item on Item packet!");
			p.disconnect();
			return;
		}
		
		ItemOnItemResult result = Engine.items.checkItemOnItem(usedItem, usedWith, false);
		
		if(result != null) {
			if(result.getResultingItem() != usedItem) {
				p.getInventory().setItem(usedSlot, new Item(result.getResultingItem(), 1));
			}
			if(result.getUsedWithResult() != usedWith) {
				p.getInventory().setItem(withSlot, new Item(result.getUsedWithResult(), 1));
			}
		}else {
			p.sendMessage("Nothing interesting happens.");
		}
	}

}
