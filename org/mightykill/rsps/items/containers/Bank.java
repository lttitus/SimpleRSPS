package org.mightykill.rsps.items.containers;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public class Bank extends Container {
	
	private int[] tabLengths = new int[9];
	public static final int MAX_SIZE = 511;	//9 bits

	public Bank(Item[] initItems) {
		super(new Item[MAX_SIZE]);
		for(int slot=0;slot<initItems.length;slot++) {
			this.items[slot] = initItems[slot];
		}
	}
	
	public Bank() {
		this(new Item[MAX_SIZE]);
	}
	
	public int depositItem(Entity e, int slot, int amount) {
		Inventory inv = e.getInventory();
		int itemId = inv.getItemInSlot(slot).getItemId();
		int removed = inv.removeAmount(itemId, amount);
		int depositedItemId = Engine.items.getDefinition(itemId).isNoted()?itemId-1:itemId;
		
		return addAmount(depositedItemId, removed, true);
	}
	
	public int withdrawItem(Entity e, int slot, int amount, boolean note) {
		Inventory inv = e.getInventory();
		int itemId = this.getItemInSlot(slot).getItemId();
		int removed = removeAmount(itemId, amount);
		int withdrawnItemId = note&&Engine.items.getDefinition(itemId).canNote()?itemId+1:itemId;
		
		return inv.addAmount(withdrawnItemId, removed, false);
	}
	
	public void appendItemToTab(int tabId, int fromSlot) {
		Item movedItem = items[fromSlot];	//Store in memory
		int appendSlot = 0;	//Calculate the slot it will be added to
		for(int tab=0;tab<=tabId;tab++) {
			appendSlot += tabLengths[tab];
		}
		
		items[fromSlot] = null;	//Delete moved item from the bank
		for(int slot=fromSlot;slot>appendSlot;slot--) {	//Re-align the items
			items[slot] = items[slot-1];
		}
		
		items[appendSlot] = movedItem;	//Add it back to the necessary slot
		tabLengths[tabId]++;
	}
	
	public int[] getTabLengths() {
		return tabLengths;
	}
	
}
