package org.mightykill.rsps.interfaces;

import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendAccessMask;
import org.mightykill.rsps.io.packets.outgoing.SendInterfaceConfig;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.io.packets.outgoing.SendScript;
import org.mightykill.rsps.io.packets.outgoing.SendString;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Inventory;
import org.mightykill.rsps.util.Misc;

/**
 * TODO: Make this play nice with NPCs??
 * @author Green
 *
 */
public class TradeInterface extends Interface {
	
	private Inventory offerItems = new Inventory();
	private Player other;
	private int offerStage = 0;	//0 = Offer screen, 1 = Accept screen
	private boolean[] accepted = new boolean[2];
	private int[][] alertIds = {{335, 36}, {334, 33}};

	public TradeInterface(Player other) {
		super(335, 0, 548, 8);
		this.other = other;
	}

	public void show(Player p) {
		if(offerStage == 0) {	//Offer screen
			p.setTabInterface(76, 336);
			
			
			p.sendPacket(new SendAccessMask(27, 0, 30, 335, 1150, 0));
			p.sendPacket(new SendAccessMask(27, 0, 32, 335, 1026, 0));
			p.sendPacket(new SendAccessMask(27, 0, 0, 336, 1278, 0));
			Object[] tparams1 = new Object[]{"", "", "", "Value", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 90, 21954590};
	        Object[] tparams2 = new Object[]{"", "", "Lend", "Value", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 22020096};
	        Object[] tparams3 = new Object[]{"", "", "", "", "", "", "", "", "Value<col=FF9040>", -1, 0, 7, 4, 90, 21954592};
	        p.sendPacket(new SendScript(150, tparams1, "IviiiIsssssssss"));
	        p.sendPacket(new SendScript(150, tparams2, "IviiiIsssssssss"));
	        p.sendPacket(new SendScript(695, tparams3, "IviiiIsssssssss"));
	        
	        p.sendPacket(new SendString("Trading with: "+other.getName(), 335, 15));
	        p.sendPacket(new SendString("", 335, 36));
	        
	        updateItems(p, other);
		}else if(offerStage == 1) {	//Accept screen
			p.setTabInterface(76, 149);
			
			p.sendPacket(new SendString("Trading with:<br>"+other.getName(), 334, 46));
			p.sendPacket(new SendInterfaceConfig(334, 45, 1));
			p.sendPacket(new SendInterfaceConfig(334, 46, 0));
			
			ArrayList<String> itemStrings = buildItemStrings();
			if(itemStrings.size() <= 16) {
				String itemsTransferred = "";
				for(String s:itemStrings) {
					itemsTransferred += (s+"<br>");
				}
				
				p.sendPacket(new SendInterfaceConfig(334, 37, 0));
				other.sendPacket(new SendInterfaceConfig(334, 41, 0));
				
				p.sendPacket(new SendString(itemsTransferred, 334, 37));
				other.sendPacket(new SendString(itemsTransferred, 334, 41));
			}else {
				String[] itemsTransferred = {"", ""};
				for(int itemSlot=0;itemSlot<itemStrings.size();itemSlot++) {
					int configSlot = (itemSlot < 16)?0:1;
					itemsTransferred[configSlot] += (itemStrings.get(itemSlot)+"<br>");
				}
				
				p.sendPacket(new SendInterfaceConfig(334, 37, 1));	//Hide middle
				p.sendPacket(new SendInterfaceConfig(334, 38, 0));
				p.sendPacket(new SendInterfaceConfig(334, 39, 0));
				other.sendPacket(new SendInterfaceConfig(334, 41, 1));	//Hide middle
				other.sendPacket(new SendInterfaceConfig(334, 42, 0));
				other.sendPacket(new SendInterfaceConfig(334, 43, 0));
				
				p.sendPacket(new SendString(itemsTransferred[0], 334, 38));
				p.sendPacket(new SendString(itemsTransferred[1], 334, 39));
				other.sendPacket(new SendString(itemsTransferred[0], 334, 42));
				other.sendPacket(new SendString(itemsTransferred[1], 334, 43));
			}
		}else {	//Log illegal action
			p.closeInterface();
		}
	}
	
	private ArrayList<String> buildItemStrings() {
		ArrayList<String> itemStrings = new ArrayList<String>();
		
		if(offerItems.getItemCount() == 0) {
			itemStrings.add("<col=FFFFFF>Absolutely nothing!");
		}else {
			for(Item item:offerItems.getItems()) {
				StringBuilder sb = new StringBuilder();
				
				if(item != null) {
					int amount = item.getItemAmount();
					if(amount > 1) {
						if(amount >= 100000 && amount < 10000000) {
							sb.append("<col=FFFFFF>");
						}else if(amount >= 10000000) {
							sb.append("<col=00FF00>");
						}
						sb.append(amount+"x ");
					}
					
					sb.append("<col=FF9040>"+Engine.items.getDefinition(item.getItemId()).getName());
					itemStrings.add(sb.toString());
				}
				
				
			}
		}
		
		return itemStrings;
	}

	public void update(Player p) {
		if(offerStage == 0) {
			this.interfaceId = 335;	//Offer
			p.showInterface(this);
		}else if(offerStage == 1) {
			this.interfaceId = 334;	//Accept
			p.showInterface(this);
		}else if(offerStage == 2) {
			complete(p);
		}else {	//Log illegal action
			cancel(p);
			return;
		}
		
		
	}

	public void close(Player p) {
		p.setTabInterface(76, 149);
		p.refreshInventory();
		if(offerStage != 2) cancel(p);
	}
	
	public int addItem(Player p, Item item) {
		int added = this.offerItems.addAmount(item, false);
		
		if(added > 0) {
			accepted[offerStage] = false;
		}
		
		this.update(p);
		return added;
	}
	
	public int removeItem(Player p, int slot, int amount) {
		Item item = offerItems.getItemInSlot(slot);
		
		if(item != null) {
			int removed = this.offerItems.removeAmount(item.getItemId(), amount);
			
			if(removed > 0) {
				accepted[offerStage] = false;
			}
			
			this.update(p);
			return removed;
		}
		
		return -1;	//Error
	}
	
	public Item getItem(int slot) {
		return this.offerItems.getItemInSlot(slot);
	}
	
	/**
	 * Sends the updated items to this Player and the other party
	 * @param p
	 */
	private void updateItems(Player p, Player other) {
		p.sendPacket(new SendItems(-1, 2, 90, this.offerItems));
		other.sendPacket(new SendItems(-2, 60981, 90, this.offerItems.clone()));
	}
	
	public void accept(Player p) {
		accepted[offerStage] = true;
		
		if(other.isTrading()) {
			TradeInterface oTrade = (TradeInterface)other.getShownInterface();
			
			if(oTrade.hasAccepted(offerStage)) {	//Continue on
				oTrade.offerStage++;
				this.offerStage++;
				
				if(this.offerStage != oTrade.offerStage) {	//Stage mismatch, TODO: log this
					p.sendMessage("Offer stage mismatch! Reverting to offer screen.");
					other.sendMessage("Offer stage mismatch! Reverting to offer screen.");
					this.offerStage = 0;
					oTrade.offerStage = 0;
				}
				
				this.update(p);
				oTrade.update(other);
			}else {	//Inform players of the action
				p.sendPacket(new SendString("Waiting for other player", alertIds[offerStage][0], alertIds[offerStage][1]));
				other.sendPacket(new SendString("Other player has accepted trade.", alertIds[offerStage][0], alertIds[offerStage][1]));
			}
		}else {	//Log illegal action
			p.closeInterface();
		}
	}
	
	public void cancel(Player p) {
		p.sendMessage("Trade declined.");
		
		boolean addedAll = true;
		for(Item item:this.offerItems.getItems()) {
			if(item != null) {
				int added = p.giveItem(item);
				
				if(added != item.getItemAmount()) {
					Position otherPos = other.getPosition();
					addedAll = false;
					Engine.groundItems.createGroundItem(item.getItemId(), item.getItemAmount()-added, otherPos.x, otherPos.y, other);
				}
			}
		}
		
		if(!addedAll) {
			other.sendMessage("<col=ab0000>Some items have dropped to the ground!");	//Not sure how this happened...
		}
		
		other.closeInterface();
	}
	
	private void complete(Player p) {
		boolean addedAll = true;
		
		for(Item item:this.offerItems.getItems()) {
			if(item != null) {
				int added = other.getInventory().addAmount(item, false);
				
				if(added != item.getItemAmount()) {
					Position otherPos = other.getPosition();
					addedAll = false;
					Engine.groundItems.createGroundItem(item.getItemId(), item.getItemAmount()-added, otherPos.x, otherPos.y, other);
				}
			}
		}
		
		p.sendMessage("Trade complete!");
		p.closeInterface();
		p.refreshInventory();
		other.refreshInventory();
		
		if(!addedAll) {
			other.sendMessage("<col=ab0000>Some items have dropped to the ground!");	//Not sure how this happened...
		}
	}
	
	public int getCurrentStage() {
		return this.offerStage;
	}
	
	public boolean hasAccepted(int stage) {
		return this.accepted[stage];
	}

}
