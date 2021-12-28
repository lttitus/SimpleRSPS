package org.mightykill.rsps.exchange;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.interfaces.Interface;

public class ExchangeInterface extends Interface {
	
	public static final int MAIN_SCREEN = -1, BUY_SCREEN = 0, SELL_SCREEN = 1;
	
	private int itemId = -1;
	private int quantity = 0, amount = 0;
	private int screenId = MAIN_SCREEN;
	private int viewSlot = -1;

	public ExchangeInterface() {
		super(105, 0, 548, 8);
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	/**
	 * The price/cost of an item
	 * @param amount
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 * Reset Item information
	 */
	private void reset() {
		this.itemId = -1;
		this.quantity = 0;
		this.amount = 0;
	}
	
	public void viewSlot(Player p, int screenId, int slot) {
		reset();	//Reset all information when moving between interfaces
		
		this.screenId = screenId;
		this.viewSlot = slot;
		
		update(p, -1);
	}
	
	public void update(Player p, long curTick) {
		p.sendConfig(1109, itemId);
		p.sendConfig(1110, quantity);
		p.sendConfig(1111, amount);
		p.sendConfig(1112, viewSlot);
		p.sendConfig(1113, screenId);
	}
	
	public int getScreenId() {
		return this.screenId;
	}
	
	public void setQuantity(int qty) {
		if(qty > 0 && qty < Integer.MAX_VALUE) {
			this.quantity = qty;
		}
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public int getViewSlot() {
		return this.viewSlot;
	}
	
	public int getItemId() {
		return this.itemId;
	}
	
	public void close(Player p) {
		if(p.getChatboxInterface() instanceof ExchangeSearch) {
			p.closeChatboxInterface();
		}
	}

	@Override
	public void show(Player p) {
		// TODO Auto-generated method stub
		
	}
}
