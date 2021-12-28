package org.mightykill.rsps.exchange.offers;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.GrandExchange;
import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public abstract class GEOffer {
	
	/** Coffer slot */
	public static final int MONEY_SLOT = 0, ITEM_SLOT = 1;
	
	/** Statuses */
	public static final int NONE = 0, SUBMITTING = 1, BUY_IN_PROGRESS = 2, BUY_CLOSED = 5;
	public static final int SALE_IN_PROGRESS = -2, SALE_CLOSED = -3;
	
	protected Entity owner;
	protected int offerSlot;
	protected int itemId, itemAmount;
	/** Coffers hold the items and the money - 0 = Money, 1 = Item */
	protected int[] coffers = new int[2];
	protected int totalAmount = 0;
	
	protected int status = 0;
	
	public GEOffer(Entity e, int slot, int itemId, int itemAmount, int status) {
		this.owner = e;
		this.offerSlot = slot;
		this.itemId = itemId;
		this.itemAmount = itemAmount;
		this.status = status;
	}
	
	public int getCofferAmount(int cofferSlot) {
		return this.coffers[cofferSlot];
	}
	
	public Entity getOwner() {
		return this.owner;
	}
	
	public int getItemId() {
		return this.itemId;
	}
	
	public int getQuantityOffered() {
		return this.itemAmount;
	}
	
	public abstract int getRemaining();
	
	/** Returns the price/cost of an offer */
	public abstract int getAmount();
	
	public int getTotalAmount() {
		return this.totalAmount;
	}
	
	/**
	 * Finalize a transaction
	 * @param tQty Item Quantity to remove from offer
	 * @param tAmt Amount the Item was procured for
	 */
	public void doTransaction(int tQty, int tAmt) {
		finalizeTransaction(tQty, tAmt);
		this.totalAmount += tQty*tAmt;
		
		if(getRemaining() <= 0) {
			closeOffer();
		}
		
		this.owner.updateExchangeOffer(this, true);
	}
	
	protected abstract void finalizeTransaction(int tQty, int tAmt);
	
	public void closeOffer() {
		this.status = (this instanceof BuyOffer?BUY_CLOSED:SALE_CLOSED);
		finalizeOffer();
		this.owner.updateExchangeOffer(this, false);
	}
	
	public void verify() {
		this.status = (this instanceof BuyOffer?BUY_IN_PROGRESS:SALE_IN_PROGRESS);
	}
	
	protected abstract void finalizeOffer();
	
	public boolean isActive() {
		return (this.status != BUY_CLOSED && this.status != SALE_CLOSED);
	}
	
	public int getSlot() {
		return this.offerSlot;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	/**
	 * Removes the Item from the coffer and gives it to the owner of this offer
	 * @param cofferSlot 0 = Money, 1 = Item
	 */
	public void takeFromCoffer(int cofferSlot) {
		if(this.coffers[cofferSlot] > 0) {
			boolean isMoney = cofferSlot == MONEY_SLOT;
			//TODO: Check for enough inventory space
			if(this.owner.giveItem(
					isMoney?995:itemId,
						coffers[cofferSlot])) {
				this.coffers[cofferSlot] = 0;
				if(owner instanceof Player) {
					((Player)owner).playSound(4040);	//plop
				}
			}
		}
		
		if(this.coffers[MONEY_SLOT] == 0 && 
				this.coffers[ITEM_SLOT] == 0 && 
				!isActive()) {	//Nothing left in this Offer, remove it
			this.owner.removeOffer(this.offerSlot);
			
			if(this.owner instanceof Player) {	//Kick the player back to the main screen
				GrandExchange.openOfferScreen((Player)owner);
			}
		}
	}
	
	public Container getCoffers() {
		Item money = this.coffers[MONEY_SLOT] > 0?new Item(995, coffers[MONEY_SLOT]):null;
		Item item = this.coffers[ITEM_SLOT] > 0?new Item(itemId, coffers[ITEM_SLOT]):null;
		Item[] items = {money, item};
		Container c = new Container(items);
		return c;
	}
	
	public void destroy() {
		this.status = 0;
	}

}
