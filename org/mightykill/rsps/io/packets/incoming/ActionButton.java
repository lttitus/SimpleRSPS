package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.ExchangeInterface;
import org.mightykill.rsps.exchange.GrandExchange;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.io.packets.outgoing.Logout;
import org.mightykill.rsps.io.packets.outgoing.PlaySound;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendInterfaceConfig;
import org.mightykill.rsps.io.packets.outgoing.SendItems;

public class ActionButton extends IncomingPacket {
	
	private Player p;

	public ActionButton(int packetId, int packetSize, byte[] data, Player origin) {
		super(packetId, packetSize, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int interfaceId = nextUnsignedShort();
		int buttonId = nextUnsignedShort();
		int buttonId2= -1;
		if(packetId == 233 || 
			packetId == 21 ||
			packetId == 169 ||
			packetId == 232) buttonId2 = nextUnsignedShort();
		
		p.idleCount = 0;
		System.out.println("Interface: "+interfaceId+"; Button: "+buttonId+"; Button2: "+buttonId2);
	
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
				
				ei.update(p, -1);
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
		}
	
	}

}
