package org.mightykill.rsps.entities.trade;

import org.mightykill.rsps.entities.Entity;

/**
 * Contains information for outgoing Trade Offers. Does not handle actual Trades.<br>
 * @see TradeInterface.java
 * @author Green
 *
 */
public class TradeOffer {
	
	private int ttl = 100;	//1 minute
	private Entity tradee;
	
	public TradeOffer(Entity tradee) {
		this.tradee = tradee;
	}
	
	public Entity getTradee() {
		return this.tradee;
	}
	
	public int age() {
		ttl--;
		return ttl;
	}
	
	public void expire() {
		ttl = 0;
	}

}
