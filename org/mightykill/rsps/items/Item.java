package org.mightykill.rsps.items;

import org.mightykill.rsps.Engine;

public class Item {
	
	private int itemId;
	private int itemAmount;
	private ItemDefinition def;
	
	public Item(int itemId, int itemAmount) {
		this.itemId = itemId;
		this.itemAmount = itemAmount;
		this.def = Engine.items.getDefinition(itemId);
	}
	
	/**
	 * Sets an amount of the item
	 * @param amount
	 * @return
	 */
	protected boolean setAmount(int amount) {
		if(amount > 1) {
			if(!def.isStackable()) {
				return false;
			}else {
				this.itemAmount = amount;
				return true;
			}
		}else if(amount == 0) {	//This is possible. Banks, chests, etc...
			this.itemAmount = 0;
			return true;
		}else {	//Anything under 0 is illegal
			return false;
		}
	}
	
	public boolean addAmount(int amount) {
		return setAmount(this.itemAmount+amount);
	}
	
	protected boolean removeAmount(int amount) {
		if(amount > 0) {
			if(itemAmount >= amount) {
				itemAmount -= amount;
				return true;
			}
		}
		
		return false;
	}
	
	public int getItemId() {
		return this.itemId;
	}
	
	public int getItemAmount() {
		return this.itemAmount;
	}
	
	public boolean isStackable() {
		return this.def.isStackable();
	}
	
	public ItemDefinition getItemDef() {
		return this.def;
	}

}
