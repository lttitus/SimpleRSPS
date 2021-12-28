package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Inventory;

public class SwitchItems extends IncomingPacket {
	
	private Player p;

	public SwitchItems(byte[] data, Player origin) {
		super(167, 9, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int toSlot = nextUnsignedShortBigEndianA();
		nextUnsignedByte();
		int fromSlot = nextUnsignedShortBigEndianA();
		nextUnsignedShort();
		int interfaceId = nextUnsignedByte();
		nextUnsignedByte();
		if(p.isDebug()) p.sendMessage(interfaceId+": "+fromSlot+"->"+toSlot);
		
		switch(interfaceId) {
		case 149:
			Inventory inv = p.getInventory();
			Item fromItem = inv.getItemInSlot(fromSlot);
			Item toItem = inv.getItemInSlot(toSlot);
			
			inv.setItem(fromSlot, toItem);
			inv.setItem(toSlot, fromItem);
			break;
		}
	}

}
