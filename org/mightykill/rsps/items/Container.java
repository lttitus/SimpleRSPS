package org.mightykill.rsps.items;

import org.mightykill.rsps.Engine;

public class Container {
	
	protected Item[] items;
	
	public Container(int size, Item[] initItems) {
		this.items = new Item[size];
		if(size > initItems.length) size = initItems.length;
		for(int i=0;i<size;i++) {
			this.items[i] = initItems[i];
		}
	}	
	
	public Container(Item[] initItems) {
		this(initItems.length, initItems);
	}
	
	public Item[] getItems() {
		return this.items;
	}
	
	public void setItem(int slot, Item item) {
		if(slot < 0 || slot > items.length) return;
		
		this.items[slot] = item;
	}
	
	/**
	 * Gets the number of unique items held in this container
	 * @return
	 */
	public int getItemCount() {
		int count = 0;
		
		for(int slot=0;slot<items.length;slot++) {
			if(items[slot] != null) {
				count++;
			}
		}
		
		return count;
	}
	
	public int getItemCount(int itemId) {
		int count = 0;
		
		for(int slot=0;slot<items.length;slot++) {
			if(items[slot] != null) {
				if(items[slot].getItemId() == itemId) count++;
			}
		}
		
		return count;
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
	
	/**
	 * Finds the first match based on itemId
	 * @param itemId
	 * @return
	 */
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
	
	public boolean containsItem(int itemId) {
		return findItemSlot(itemId) != -1;
	}
	
	public boolean hasAmount(int itemId, int amount) {
		int slot = findItemSlot(itemId);
		return items[slot].getItemAmount() >= amount;
	}
	
	/**
	 * 
	 * @param itemId
	 * @param amount
	 * @param forceStack Stack the first matching item slot, even if it is not usually stackable. E.g. Banking
	 * @return The amount we were able to add, or -1 if there are no free slots
	 */
	public int addAmount(int itemId, int amount, boolean forceStack) {
		if(Engine.items.getDefinition(itemId).isStackable() || forceStack) {	//Notes are defined as stackable as well, so they don't need to be checked individually
			final int MAX = Integer.MAX_VALUE;
			int slot = findItemSlot(itemId);
			
			if(slot != -1) {
				int curAmount = items[slot].getItemAmount();
				long newAmount = curAmount+amount;
				
				if(newAmount <= MAX) {
					items[slot].setAmount((int)newAmount);
					return amount;
				}else {
					int possible = (int) (newAmount-MAX);
					
					items[slot].setAmount(MAX);
					return possible;
				}
			}else {
				int freeSlot = getNextFreeSlot(itemId);
				
				if(freeSlot != -1) {
					if(amount > MAX) amount = MAX;
					setItem(freeSlot, new Item(itemId, amount));
					return amount;
				}else {
					return -1;	//Error
				}
			}
		}else {	//Non-stackable
			int freeSlot = getNextFreeSlot(itemId);
			
			if(freeSlot != -1) {
				setItem(freeSlot, new Item(itemId, 1));
				
				if(amount > 1) {
					return 1+addAmount(itemId, amount-1, forceStack);
				}else {
					return 1;
				}
			}else {
				return -1;	//Error
			}
		}
	}
	
	public int addAmount(Item item, boolean forceStack) {
		return addAmount(item.getItemId(), item.getItemAmount(), forceStack);
	}
	
	/**
	 * 
	 * @param itemId
	 * @param amount
	 * @return The amount we were able to remove, or -1 if the item was not found
	 */
	public int removeAmount(int itemId, int amount, boolean removeZero) {
		if(Engine.items.getDefinition(itemId).isStackable()) {
			int slot = findItemSlot(itemId);
			
			if(slot != -1) {
				int curAmount = items[slot].getItemAmount();
				int newAmount = curAmount-amount;
				
				if(newAmount >= 0) {
					items[slot].setAmount(curAmount-amount);
					if(newAmount == 0 && removeZero) {
						items[slot] = null;
					}
					return amount;
				}else {
					int possible = curAmount;
					
					items[slot].setAmount(0);
					if(removeZero) {
						items[slot] = null;
					}
					
					return possible;
				}
			}else {	//Not found
				return -1;
			}
		}else {
			int slot = findItemSlot(itemId);
			
			if(slot != -1) {
				items[slot].setAmount(0);
				if(removeZero) {
					items[slot] = null;
				}
					
				if(amount > 1) {
					return 1+removeAmount(itemId, amount-1, removeZero);
				}else {
					return 1;
				}
				
			}else {	//Not found
				return -1;
			}
		}
	}
	
	public int removeAmount(int itemId, int amount) {
		return removeAmount(itemId, amount, true);
	}
	
	/*public boolean removeAmount(Item item, int amount) {
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
	}*/
	
	public Item getItemInSlot(int slot) {
		return items[slot];
	}
	
	/*public boolean addItem(Item item) {
		int existingItemSlot = findItemSlot(item.getItemId());
		
		if(existingItemSlot != -1 && items[existingItemSlot].isStackable()) {	//Found
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
	}*/
	
	public Container clone() {
		return new Container(this.items);
	}

}
