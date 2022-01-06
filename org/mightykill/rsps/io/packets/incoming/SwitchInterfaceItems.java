package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.interfaces.BankInterface;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Bank;
import org.mightykill.rsps.items.containers.Inventory;

public class SwitchInterfaceItems extends IncomingPacket {
	
	private Player p;

	public SwitchInterfaceItems(byte[] data, Player origin) {
		super(179, 12, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int combinedToId = nextInt();	//interface << 16 | childId
		int toInterfaceId = combinedToId >> 16;
		int toChildId = combinedToId & 0xFF;
		
		int combinedFromId = nextInt();	//interface << 16 | childId
		int fromInterfaceId = combinedFromId >> 16;
		int fromChildId = combinedFromId & 0xFF;
		
		int fromSlot = nextUnsignedShort();
		int toSlot = nextSignedShortBigEndian();
		
		p.debug("("+fromInterfaceId+":"+fromChildId+" -> "+toInterfaceId+":"+toChildId+"): "+fromSlot+" -> "+toSlot);
		
		switch(fromInterfaceId) {
		case 762:	//Bank screen
			if(p.isBanking()) {
				BankInterface bankInterface = (BankInterface)p.getShownInterface();
				Bank pBank = p.getBank();
				
				if(fromChildId == 73) {
					if(toSlot == -1) {	//Sending item to another tab
						int tabId = 41-toChildId >> 1;
						
						pBank.appendItemToTab(tabId, fromSlot);
					}else {	//Switching items
						Item fromItem = pBank.getItemInSlot(fromSlot);
						Item toItem = pBank.getItemInSlot(toSlot);
						
						pBank.setItem(fromSlot, toItem);
						pBank.setItem(toSlot, fromItem);
					}
					
					bankInterface.update(p);
				}
			}else {	//Log illegal action
				
			}
			break;
		case 763:	//Inventory when banking
			Inventory inv = p.getInventory();
			Item fromItem = inv.getItemInSlot(fromSlot);
			Item toItem = inv.getItemInSlot(toSlot);
			
			inv.setItem(fromSlot, toItem);
			inv.setItem(toSlot, fromItem);
			break;
		}
	}

}
