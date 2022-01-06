package org.mightykill.rsps.intents;

import org.mightykill.rsps.entities.Entity;

public class TradeIntent extends Intent {
	
	private Entity tradee;

	public TradeIntent(Entity trader, Entity tradee) {
		super(trader);
		this.tradee = tradee;
	}

	public boolean handleIntent() {
		return false;
	}

	public void finishIntent() {
		
	}

}
