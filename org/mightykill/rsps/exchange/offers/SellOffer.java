package org.mightykill.rsps.exchange.offers;

import org.mightykill.rsps.entities.Entity;

public class SellOffer extends GEOffer {
	
	private int price;
	private int soldAmount = 0;

	public SellOffer(Entity e, int slot, int itemId, int itemAmount, int price, int status) {
		super(e, slot, itemId, itemAmount, status);
		this.price = price;
	}
	
	public int getPrice() {
		return this.price;
	}
	
	public int getRemaining() {
		return this.itemAmount-soldAmount;
	}

	/**
	 * Make a sale
	 */
	protected void finalizeTransaction(int tQty, int tAmt) {
		this.soldAmount += tQty;
		this.coffers[MONEY_SLOT] += tAmt*tQty;
	}

	public int getAmount() {
		return this.price;
	}

	protected void finalizeOffer() {
		if(soldAmount < itemAmount) {
			this.coffers[ITEM_SLOT] += getRemaining();
		}
	}

}
