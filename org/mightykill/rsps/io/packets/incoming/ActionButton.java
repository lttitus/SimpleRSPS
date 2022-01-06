package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.ExchangeInterface;
import org.mightykill.rsps.exchange.GrandExchange;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.interfaces.BankInterface;
import org.mightykill.rsps.interfaces.TradeInterface;
import org.mightykill.rsps.io.packets.outgoing.Logout;
import org.mightykill.rsps.io.packets.outgoing.PlaySound;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendInterfaceConfig;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Bank;
import org.mightykill.rsps.items.containers.Inventory;

public class ActionButton extends IncomingPacket {
	
	private Player p;

	public ActionButton(int packetId, int packetSize, byte[] data, Player origin) {
		super(packetId, packetSize, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int interfaceId = nextUnsignedShort();
		int buttonId = nextSignedShort();
		int buttonId2= -1;
		if(packetId == 233 || 
			packetId == 21 ||
			packetId == 169 ||
			packetId == 232 || packetId == 214) buttonId2 = nextSignedShort();
		
		p.idleCount = 0;
		p.debug(packetId+" - Interface: "+interfaceId+"; Button: "+buttonId+"; Button2: "+buttonId2);
		System.out.println(packetId+" - Interface: "+interfaceId+"; Button: "+buttonId+"; Button2: "+buttonId2);
		
		switch(interfaceId) {
		case 89:	//Attack style tabs
			if(buttonId == 10) {	//Special attack
				p.getCombat().toggleSpecialAttack();
			}
			break;
		case 92:	//Attack interface
			if(buttonId == 24) {
				p.getCombat().setRetaliate(!p.getCombat().willRetaliate());
				p.sendPacket(new SendConfig(172, p.getCombat().willRetaliate()?0:1));
			}
			break;
		case 105:	//GE Interface
			ExchangeInterface ei = ((ExchangeInterface)p.getShownInterface());
			
			if(ei != null) {
				if(buttonId == 13) {	//Close interface
					p.closeInterface();
				}else if(buttonId == 18 || buttonId == 34 || buttonId == 50 ||
					buttonId == 69 || buttonId == 88 || buttonId == 107) {	//View offer
					int offerSlot = -1;
					
					/* Find slot that was clicked */
					if(buttonId == 18 || buttonId == 34 || buttonId == 50) {
						offerSlot = (buttonId - 18)/16;
					}else if(buttonId == 69 || buttonId == 88 || buttonId == 107) {
						offerSlot = 3 + ((buttonId - 69)/19);
					}
					
					if(offerSlot != -1) {
						GrandExchange.viewOffer(p, offerSlot);
					}	//Else, ignore it?
				}else if(buttonId == 30 || buttonId == 46 || buttonId == 62 ||
					buttonId == 81 || buttonId == 100 || buttonId == 119) {	//Buy intent
					int offerSlot = -1;
					
					/* Find slot that was clicked */
					if(buttonId == 30 || buttonId == 46 || buttonId == 62) {
						offerSlot = (buttonId - 30)/16;
					}else if(buttonId == 81 || buttonId == 100 || buttonId == 119) {
						offerSlot = 3 + ((buttonId - 81)/19);
					}
					
					if(offerSlot != -1) {
						GrandExchange.openBuyOffer(p, offerSlot);
						GrandExchange.openItemSearch(p);
					}	//Else, ignore it?
				}else if(buttonId == 31 || buttonId == 47 || buttonId == 63 ||
					buttonId == 82 || buttonId == 101 || buttonId == 120) {	//Sell intent
					int offerSlot = -1;
					
					/* Find slot that was clicked */
					if(buttonId == 31 || buttonId == 47 || buttonId == 63) {
						offerSlot = (buttonId - 31)/16;
					}else if(buttonId == 82 || buttonId == 101 || buttonId == 120) {
						offerSlot = 3 + ((buttonId - 82)/19);
					}
					
					if(offerSlot != -1) {
						GrandExchange.openSellOffer(p, offerSlot);
					}	//Else, ignore it?
				}else if(buttonId == 127) {	//Back button in offer
					p.closeChatboxInterface();
					ei.viewSlot(p, ExchangeInterface.MAIN_SCREEN, -1);
				}else if(buttonId == 157 || buttonId == 159) {	//+/- 1 qty
					int qtyChange = (buttonId == 157?-1:1);
					
					ei.setQuantity(ei.getQuantity()+qtyChange);
				}else if(buttonId == 162 || buttonId == 164 || buttonId == 166 || buttonId == 168) {	//Add x/all
					int qtyChange = (buttonId == 162?1:
										(buttonId == 164?10:100));
					
					if(buttonId == 168) {
						if(ei.getScreenId() == ExchangeInterface.BUY_SCREEN) {
							qtyChange = 1000;
							ei.setQuantity(ei.getQuantity()+qtyChange);
						}else if(ei.getScreenId() == ExchangeInterface.SELL_SCREEN) {
							qtyChange = 1000;	//Get Inventory count
							ei.setQuantity(qtyChange);
						}
					}else {
						ei.setQuantity(ei.getQuantity()+qtyChange);
					}
				}else if(buttonId == 170 || buttonId == 185) {	//Custom quantity/Custom price
					
				}else if(buttonId == 171 || buttonId == 173) {	//+/- 1 price
					int amtChange = (buttonId == 171?-1:1);
					
					ei.setAmount(ei.getAmount()+amtChange);
				}else if(buttonId == 177 || buttonId == 180 || buttonId == 183) {	//+/- %5/med
					int amount = ei.getAmount();
					
					if(buttonId == 177) {	//- %5
						ei.setAmount((int)Math.ceil(amount*0.95));
					}else if(buttonId == 180) {	//Med price
						
					}else {
						ei.setAmount((int)Math.ceil(amount*1.05));
					}
				}else if(buttonId == 190) {	//Confirm offer
					boolean isBuy = ei.getScreenId() == ExchangeInterface.BUY_SCREEN;
					
					GrandExchange.createOffer(p, ei, isBuy);
				}else if(buttonId == 194) {	//Buy item search
					GrandExchange.openItemSearch(p);
				}else if(buttonId == 203) {
					int viewSlot = ei.getViewSlot();
					
					GEOffer offer = p.getOffer(viewSlot);
					if(offer != null) {
						offer.closeOffer();
					}
				}else if(buttonId == 209 || buttonId == 211) {
					int viewSlot = ei.getViewSlot();
					
					GEOffer offer = p.getOffer(viewSlot);
					if(offer != null) {
						int cofferSlot = (buttonId == 209?
								GEOffer.MONEY_SLOT:GEOffer.ITEM_SLOT);
						
						offer.takeFromCoffer(cofferSlot);
						p.sendPacket(new SendItems(-1, -1757-viewSlot, 523+viewSlot, offer.getCoffers()));
					}
				}
				
				ei.update(p);
			}else {
				p.sendMessage("You should not be seeing this message!");
			}
			break;
		case 109:
			
			break;
		case 182:
			if(buttonId == 6) {
				if(!p.getCombat().isInCombat()) {
					origin.sendPacket(new Logout());
				}else {
					p.sendMessage("You cannot logout within 10 seconds of being in combat!");
				}
			}
			break;
		case 274:
			if(buttonId == 7) {
				p.sendMessage("Testing config "+p.confTest);
				p.sendPacket(new SendConfig(p.confTest, p.confTest));
				p.confTest++;
			}else if(buttonId == 8) {
				p.sendMessage("Testing blink "+p.testBlink);
				p.blinkSkillIcon(p.testBlink);
				p.testBlink++;
			}else if(buttonId == 9) {
				p.sendMessage("Testing sound "+p.testSound);
				p.sendPacket(new PlaySound(p.testSound, p.testSoundByte));
				p.testSound++;
			}else if(buttonId == 10) {
				p.sendMessage("Testing geconf "+p.testgeconf);
				p.sendPacket(new SendInterfaceConfig(389, p.testgeconf, 0));
				p.testgeconf++;
			}
			break;
		case 334:	//Trade accept screen
			if(buttonId == 20) {
				if(p.isTrading()) {
					TradeInterface trade = (TradeInterface)p.getShownInterface();
					
					trade.accept(p);
				}else {	//Log illegal action
					
				}
			}else if(buttonId == 21) {
				if(p.isTrading()) {
					TradeInterface trade = (TradeInterface)p.getShownInterface();
					
					p.closeInterface();
				}else {	//Log illegal action
					
				}
			}
			break;
		case 335:	//Trade offer screen
			if(p.isTrading()) {
				TradeInterface trade = (TradeInterface)p.getShownInterface();
				
				if(buttonId == 12) {
					p.closeInterface();
				}else if(buttonId == 16) {
					trade.accept(p);
				}else if(buttonId == 18) {
					p.closeInterface();
				}else if(buttonId == 30) {	//Remove item
					int slot = buttonId2;
					Item removing = trade.getItem(slot);
					int amount = 1;
					if(packetId == 233) {
						amount = 1;
					}else if(packetId == 21) {
						amount = 5;
					}else if(packetId == 169) {
						amount = 10;
					}else if(packetId == 214) {
						amount = removing.getItemAmount();
					}
					
					if(removing != null) {
						int removed = trade.removeItem(p, slot, amount);
						int added = p.getInventory().addAmount(removing.getItemId(), removed, false);
						
						if(added > 0) {
							p.debug("Removed "+removed+" from trade screen");
							p.refreshInventory();
							p.sendPacket(new SendItems(-1, 1, 93, p.getInventory()));
						}else {
							p.sendMessage("You do not have enough inventory space to remove this item.");	//How did they do this...?
						}
					}else {	//Log illegal action
						p.sendMessage("You cannot remove what you didn't offer!");
					}
				}
			}else {	//Log illegal action
				p.closeInterface();
			}
			break;
		case 336:	//Trade inventory side bar
			if(p.isTrading()) {
				TradeInterface trade = (TradeInterface)p.getShownInterface();
				
				if(buttonId == 0) {
					int slot = buttonId2;
					Item adding = p.getInventory().getItemInSlot(slot);
					
					if(adding != null) {
						int amount = 1;
						int maxAmount = adding.getItemAmount();
						if(!Engine.items.getDefinition(adding.getItemId()).isStackable()) {
							maxAmount = p.getInventory().getItemCount(adding.getItemId());
						}
						
						if(packetId == 233) {
							amount = 1;
						}else if(packetId == 21) {
							amount = 5;
						}else if(packetId == 169) {
							amount = 10;
						}else if(packetId == 214) {
							amount = maxAmount;
						}
						
						if(amount > maxAmount) {
							p.sendMessage("You do not have that many to give!");
							amount = maxAmount;
						}
						
						int added = trade.addItem(p, new Item(adding.getItemId(), amount));
						if(added > 0) {
							int removed = p.getInventory().removeAmount(adding.getItemId(), added);
							
							p.debug("Removed "+removed+" from inventory");
							p.refreshInventory();
							p.sendPacket(new SendItems(-1, 1, 93, p.getInventory()));
						}else {
							p.sendMessage("Trade screen is too full to add this item!");	//How did they do this...?
						}
					}else {
						p.sendMessage("You cannot add what you don't have!");
					}
				}
			}else {	//Log illegal action
				p.closeInterface();
			}
			break;
		case 389:	//Grand Exchange Search
			if(buttonId == 10) {
				p.closeChatboxInterface();
			}
			break;
		case 750:
			if(buttonId == 1) {
				p.getMovement().setRun(!p.getMovement().isRunning());	//Toggle run
				p.sendPacket(new SendConfig(173, p.getMovement().isRunning()?1:0));	//Sync Client screen to Server; prevents spamming
			}
			break;
		case 762:	//Bank
			if(buttonId == 22) {	//Close
				p.closeInterface();
			}else if(buttonId == 16) {
				BankInterface bank = (BankInterface)p.getShownInterface();
				
				bank.setWithdrawalMode(!bank.willNote());
			}
			
			if(p.isBanking()) {
				BankInterface bank = (BankInterface)p.getShownInterface();
				Bank pBank = p.getBank();
				
				int bankSlot = buttonId-73;
				if(bankSlot >= 0) {
					int availableAmount = pBank.getItemInSlot(bankSlot).getItemAmount();
					int withdraw = 0;
					
					if(packetId == 233) {
						withdraw = 1;
					}else if(packetId == 21) {
						withdraw = 5;
					}else if(packetId == 169) {
						withdraw = 10;
					}else if(packetId == 232) {
						withdraw = availableAmount;
					}
					
					if(withdraw > availableAmount) {
						withdraw = availableAmount;
					}
					
					if(pBank.withdrawItem(p, bankSlot, withdraw, bank.willNote()) != withdraw) {
						p.sendMessage("You do not have enough inventory space to withdraw that many.");
					}
					
					bank.update(p);
				}
			}else {	//Log illegal action
				
			}
			break;
		case 763:	//Inventory when banking (deposit items)
			if(p.isBanking()) {
				BankInterface bank = (BankInterface)p.getShownInterface();
				Bank pBank = p.getBank();
				int availableAmount = p.getInventory().getItemInSlot(buttonId2).getItemAmount();
				int deposit = 0;
				
				if(packetId == 233) {
					deposit = 1;
				}else if(packetId == 21) {
					deposit = 5;
				}else if(packetId == 169) {
					deposit = 10;
				}else if(packetId == 232) {
					deposit = availableAmount;
				}
				
				if(deposit > availableAmount) {
					deposit = availableAmount;
				}
				
				if(pBank.depositItem(p, buttonId2, deposit) != deposit) {
					p.sendMessage("There is not enough room in your bank to store this item.");
				}
				
				bank.update(p);
			}else {	//Log illegal action
				
			}
			break;
		}
		
	}

}
