package org.mightykill.rsps.items;

import org.mightykill.rsps.Engine;

public class Container {
	
	protected Item[] items;
	
	public Container(Item[] initItems) {
		this.items = initItems;
	}
	
	public Item[] getItems() {
		return this.items;
	}
	
	public void setItem(int slot, Item item) {
		if(slot < 0 || slot > items.length) return;
		
		this.items[slot] = item;
	}
	
	protected int getNextFreeSlot(int itemId) {
		boolean canStack = Engine.items.getDefinition(itemId).isStackable();
		
		for(int i=0;i<items.length;i++) {
			if(items[i] == null) {
				return i;
			}else {
				if(canStack &&
					items[i].getItemId() == itemId) {
					return i;
				}
			}
		}
		
		return -1;
	}
	
	public int findItemSlot(int itemId) {
		for(int i=0;i<items.length;i++) {
			Item item = items[i];
			if(item != null && item.getItemId() == itemId) {
				return i;
			}
		}
		
		 return -1;
	}
	
	public Item getItem(int itemId) {
		for(Item item:items) {
			if(item != null) {
				if(item.getItemId() == itemId) {
					return item;
				}
			}
		}
		
		return null;
	}
	
	public boolean removeAmount(Item item, int amount) {
		int slot = findItemSlot(item.getItemId());
			
		if(slot != -1) {
			boolean stack = item.isStackable();
			
			if(!stack) {
				item.setAmount(0);
				items[slot] = null;
				
				return removeAmount(item, amount-1);
			}else {
				boolean removed = item.removeAmount(amount);
				
				if(removed) {
					if(item.getItemAmount() <= 0) {
						items[slot] = null;
					}
				}
				
				return removed;
			}
		}
		
		return false;
	}
	
	public Item getItemInSlot(int slot) {
		return items[slot];
	}
	
	public boolean addItem(Item item) {
		int existingItemSlot = findItemSlot(item.getItemId());
		
		if(existingItemSlot != -1 && 
			items[existingItemSlot].isStackable()) {	//Found
			items[existingItemSlot].addAmount(item.getItemAmount());
				
			return true;
		}else {
			int emptySlot = getNextFreeSlot(item.getItemId());
			
			if(emptySlot != -1) {
				items[emptySlot] = new Item(item.getItemId(), 1);
				
				if(item.getItemAmount()-1 > 0) {
					addItem(item.getItemId(), item.getItemAmount()-1);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean addItem(int itemId, int itemAmount) {
		return addItem(new Item(itemId, itemAmount));
	}

}
