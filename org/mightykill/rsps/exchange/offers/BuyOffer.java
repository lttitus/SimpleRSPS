package org.mightykill.rsps.exchange.offers;

import org.mightykill.rsps.entities.Entity;

public class BuyOffer extends GEOffer {
	
	private int cost;
	private int boughtAmount = 0;

	public BuyOffer(Entity e, int slot, int itemId, int itemAmount, int cost, int status) {
		super(e, slot, itemId, itemAmount, status);
		this.cost = cost;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public int getRemaining() {
		return this.itemAmount-boughtAmount;
	}

	protected void finalizeTransaction(int tQty, int tAmt) {
		this.boughtAmount += tQty;
		if(tAmt < cost) coffers[MONEY_SLOT] += (cost-tAmt)*tQty;	//Give us back the difference
		coffers[ITEM_SLOT] += tQty;
	}

	public int getAmount() {
		return this.cost;
	}

	protected void finalizeOffer() {
		if(boughtAmount < itemAmount) {
			this.coffers[MONEY_SLOT] += getRemaining()*cost;
		}
	}

}
