package org.mightykill.rsps.exchange;

import java.sql.Connection;
import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.offers.BuyOffer;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.exchange.offers.SellOffer;
import org.mightykill.rsps.interfaces.Interface;
import org.mightykill.rsps.io.packets.outgoing.SendItems;
import org.mightykill.rsps.io.packets.outgoing.SendScript;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Inventory;

/**
 * Manages everything for the GE.<br>
 * TODO: Break the offer management out to an external server when we implement > 1 world and UUIDs
 * @author Green
 *
 */
public class GrandExchange {
	
	private Connection sql;
	private ArrayList<GEOffer> worldOffers = new ArrayList<GEOffer>();
	
	public GrandExchange(Connection sqlConnection) {
		this.sql = sqlConnection;
	}
	
	public void process(long curTick) {
		if(curTick % 25 == 0) {	//Every 15 seconds - Check for backlogged offer completions
			checkOffersForTransaction(worldOffers);
		}
	}
	
	public void checkOffersForTransaction(ArrayList<GEOffer> offers) {
		GEOffer[] offersList = new GEOffer[offers.size()];
		offersList = offers.toArray(offersList);
		
		for(GEOffer offer:offersList) {
			if(offer.isActive()) {
				ArrayList<GEOffer> itemOffers = getOffersByItem(offer.getItemId());
				
				if(offer instanceof BuyOffer) {	//We are looking to Buy an Item - take first low offer
					int ltb = offer.getRemaining();
					
					if(ltb > 0) {
						BuyOffer req = (BuyOffer)offer;	//Requisition
						
						for(GEOffer testOffer:itemOffers) {
							if(testOffer instanceof SellOffer) {
								if(testOffer.isActive()) {
									int lts = testOffer.getRemaining();
									
									if(lts > 0) {
										SellOffer sale = (SellOffer)testOffer;
										int salePrice = sale.getPrice();
										int reqCost = req.getCost();
										
										if(salePrice <= reqCost) {	//Viable transaction - Take first lowest sale price; TODO: Take lowest first
											int transactionAmount = ltb <= lts?ltb:lts;	//If we are buying more than what is offered, buy what we can, else buy all we need
	
											sale.doTransaction(transactionAmount, reqCost);
											req.doTransaction(transactionAmount, reqCost);
										}	//Price is too high
									}
								}	//No more items left to sell
							}
						}
					}	//We have bought all of the items we requested
				}else {	//We are looking to make a Sale
					int lts = offer.getRemaining();
					
					if(lts > 0) {
						SellOffer sale = (SellOffer)offer;
						
						for(GEOffer testOffer:itemOffers) {
							if(testOffer instanceof BuyOffer) {
								if(testOffer.isActive()) {
									int ltb = testOffer.getRemaining();
									
									if(ltb > 0) {
										BuyOffer req = (BuyOffer)testOffer;
										int reqCost = req.getCost();
										int salePrice = sale.getPrice();
										
										if(reqCost >= salePrice) {	//Viable transaction - Take first highest buy price; TODO: Take highest first
											int transactionAmount = lts <= ltb?lts:ltb;
											
											req.doTransaction(transactionAmount, salePrice);
											sale.doTransaction(transactionAmount, salePrice);
										}
									}
								}
							}
						}
					}
				}
			}	//Buy offer is no longer active
		}
	}
	
	public void addOffer(GEOffer offer, int slot) {
		offer.getOwner().updateExchangeOffer(offer, false);
		if(worldOffers.add(offer)) {
			offer.verify();
			offer.getOwner().setOffer(slot, offer);
			offer.getOwner().updateExchangeOffer(offer, false);
			checkOffersForTransaction(
					getOffersByItem(
							offer.getItemId()));
		}
	}
	
	public static void createOffer(Player p, ExchangeInterface ei, boolean isBuy) {
		boolean handled = false;
		
		if(ei.getItemId() > -1 && ei.getItemId() != 995) {
			if(ei.getAmount() > 0 && ei.getAmount() <= Integer.MAX_VALUE) {
				if(ei.getQuantity() > 0 && ei.getAmount() <= Integer.MAX_VALUE) {
					Inventory inv = p.getInventory();
					
					if(isBuy) {
						Item money = inv.getItem(995);
						
						if(money != null) {
							if(money.getItemAmount() >= ei.getAmount()) {
								Engine.ge.addOffer(new BuyOffer(p, ei.getViewSlot(), ei.getItemId(), ei.getQuantity(), ei.getAmount(), 1), ei.getViewSlot());
								inv.removeAmount(money, ei.getAmount());
								handled = true;
							}else {
								p.sendMessage("You don't have enough money to complete this offer.");
							}
						}else {
							p.sendMessage("You don't have enough money to complete this offer.");
						}
					}else {
						Item item = inv.getItem(ei.getItemId());
						int itemId = ei.getItemId()+(Engine.items.getDefinition(ei.getItemId()).isNoted()?-1:0);
						
						if(item != null) {
							if(item.getItemAmount() >= ei.getAmount()) {
								Engine.ge.addOffer(new SellOffer(p, ei.getViewSlot(), itemId, ei.getQuantity(), ei.getAmount(), 1), ei.getViewSlot());
								inv.removeAmount(item, ei.getAmount());
								handled = true;
							}
						}
					}
					
					
				}else {	//Must requisition atleast 1
					p.sendMessage("You must "+(isBuy?"buy":"sell")+" atleast one item.");
				}
			}else {	//Must requisition for > 0
				p.sendMessage("You must "+(isBuy?"buy":"sell")+" an item for atleast 1 coin.");
			}
		}else {	//No item chosen
			p.sendMessage("You must choose a valid item to "+(isBuy?"buy":"sell")+".");
		}
		
		if(handled) {
			ei.viewSlot(p, ExchangeInterface.MAIN_SCREEN, -1);
			p.refreshInventory();
			p.playSound(4043);
		}else {
			p.playSound(4039);
		}
	}
	
	public ArrayList<GEOffer> getOffersByItem(int itemId) {
		GEOffer[] offersList = new GEOffer[worldOffers.size()];
		offersList = worldOffers.toArray(offersList);
		ArrayList<GEOffer> results = new ArrayList<GEOffer>();
		
		for(GEOffer offer:offersList) {
			if(offer.getItemId() == itemId) {
				results.add(offer);
			}
		}
		
		return results;
	}
	
	/**
	 * Sends the packets necessary to open a fresh GE Offer Screen
	 * @param p
	 */
	public static void openOfferScreen(Player p) {
		
		ExchangeInterface ex = new ExchangeInterface();
		p.showInterface(ex);
		ex.viewSlot(p, ExchangeInterface.MAIN_SCREEN, -1);
		
	}
	
	public static void openBuyOffer(Player p, int slot) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			if(ci instanceof ExchangeInterface) {
				((ExchangeInterface) ci).viewSlot(p, ExchangeInterface.BUY_SCREEN, slot);
			}else {
				p.sendMessage("Please finish what you are doing before checking your offers.");
			}
		}else {	//Try opening it again
			openOfferScreen(p);
			openBuyOffer(p, slot);
		}
	}
	
	public static void openSellOffer(Player p, int slot) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			if(ci instanceof ExchangeInterface) {
				((ExchangeInterface) ci).viewSlot(p, ExchangeInterface.SELL_SCREEN, slot);
			}else {
				p.sendMessage("Please finish what you are doing before checking your offers.");
			}
		}else {
			openOfferScreen(p);
			openSellOffer(p, slot);
		}
	}
	
	public static void viewOffer(Player p, int slot) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			if(ci instanceof ExchangeInterface) {
				((ExchangeInterface) ci).viewSlot(p, ExchangeInterface.MAIN_SCREEN, slot);
				GEOffer offer = p.getOffer(slot);
				
				if(offer != null) {
					p.sendPacket(new SendItems(-1, -1757-slot, 523+slot, offer.getCoffers()));
				}
			}else {
				p.sendMessage("Please finish what you are doing before checking your offers.");
			}
		}else {
			openOfferScreen(p);
			viewOffer(p, slot);
		}
	}
	
	public static void openItemSearch(Player p) {
		Interface chatInterface = p.getChatboxInterface();
		
		if(chatInterface == null) {
			p.showChatboxInterface(new ExchangeSearch());
			p.sendPacket(new SendScript(570, new Object[] {"Grand Exchange Item Search"}, "s"));
		}	//Would not do any good to send them a message, as there is something on their chatbox
	}
	
	public static void openItemSetsScreen(Player p) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			p.sendMessage("Please finish what you are doing before opening this interface.");
		}else {
			p.showInterface(new ExchangeSets());
		}
	}
	
	public static void openHistory(Player p) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			p.sendMessage("Please finish what you are doing before opening this interface.");
		}else {
			p.showInterface(new ExchangeHistory());
		}
	}
	
	public static void openCollection(Player p) {
		Interface ci = p.getShownInterface();
		
		if(ci != null) {
			p.sendMessage("Please finish what you are doing before opening this interface.");
		}else {
			p.showInterface(new ExchangeCollect());
		}
	}
	
	public static void updateOfferScreen(Player p, int itemId) {
		p.closeChatboxInterface();
		((ExchangeInterface)p.getShownInterface()).setItemId(itemId);
		((ExchangeInterface)p.getShownInterface()).update(p, -1);
		//p.sendConfig(1109, itemId);
	}

}
