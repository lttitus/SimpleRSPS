package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.items.Item;

public class DropItem extends IncomingPacket {
	
	private Player p;

	public DropItem(byte[] data, Player origin) {
		super(211, 8, data, origin.getClient());
		this.p = origin;
		
		
	}

	public void handlePacket() {
		int junk = nextInt();
		int slot = nextUnsignedShortBigEndianA() & 0xFFFF;
		int id = nextUnsignedShort() & 0xFFFF;
		Item item = p.getInventory().getItemInSlot(slot);
		
		if(item.getItemId() == id) {
			p.dropItem(slot);
			p.refreshInventory();
			p.playSound(2739);
		}else {
			//TODO: Log an illegal action
		}
	}

}
