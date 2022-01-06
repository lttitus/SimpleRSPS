package org.mightykill.rsps.interfaces;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendAccessMask;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Bank;
import org.mightykill.rsps.items.containers.Inventory;

public class BankInterface extends Interface {
	
	private boolean replace = true;
	private boolean noted = false;
	private int visibleTabId = 0;

	public BankInterface() {
		super(762, 0, 548, 8);
	}

	public void show(Player p) {
		p.sendPacket(new SendConfig(563, 4194304));
		p.sendPacket(new SendConfig(563, -2013265920));
		p.setTabInterface(76, 763);
		p.sendPacket(new SendAccessMask(496, 0, 73, 762, 1278, 20)); // Bank options
		p.sendPacket(new SendAccessMask(27, 0, 0, 763, 1150, 18)); // Inventory options (deposit)
		
		
		update(p);
	}

	public void update(Player p) {
		Bank pBank = p.getBank();
		Inventory pInv = p.getInventory();
		
		sendTabLengths(p);
		
		p.sendPacket(new SendItems(-1, 64207, 95, pBank));
		p.sendPacket(new SendItems(-1, 64207, 93, pInv));
		
		p.sendConfig(115, noted?1:0);
		p.sendConfig(304, replace?0:1);
	}

	public void close(Player p) {
		p.setTabInterface(76, 149);
		p.refreshInventory();
	}
	
	/**
	 * 
	 * @param p
	 */
	public void sendTabLengths(Player p) {
		int[] tabs = p.getBank().getTabLengths();
		
		/* Quick and dirty, but effective */
		p.sendConfig(1248, tabs[8] << 10 | tabs[7]);
		p.sendConfig(1247, tabs[6] << 20 | tabs[5] << 10 | tabs[4]);
		p.sendConfig(1246, tabs[3] << 20 | tabs[2] << 10 | tabs[1]);
	}
	
	public int getVisibleTab() {
		return this.visibleTabId;
	}
	
	public void viewTab(int tab) {
		this.visibleTabId = tab;
	}
	
	public void setWithdrawalMode(boolean note) {
		this.noted = note;
	}
	
	public boolean willNote() {
		return this.noted;
	}
	
	public void setDepositMode(boolean replace) {
		this.replace = replace;
	}
	
	public boolean willReplace() {
		return this.replace;
	}

}
