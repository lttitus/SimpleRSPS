package org.mightykill.rsps.items.useresults;

public class ItemUseResult {
	
	protected int usedItem, usedResult;
	
	public ItemUseResult(int usedItem, int usedResult) {
		this.usedItem = usedItem;
		this.usedResult = usedResult;
	}
	
	public int getUsedItem() {
		return this.usedItem;
	}
	
	public int getResultingItem() {
		return this.usedResult;
	}

}
