package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.trade.TradeOffer;
import org.mightykill.rsps.io.client.Client;

public class AcceptTradeRequest extends IncomingPacket {
	
	private Player p;

	public AcceptTradeRequest(byte[] data, Player origin) {
		super(253, 1, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int worldId = nextUnsignedByte()-128;
		Player trader = Engine.players.getPlayer(worldId);
		
		if(trader != null) {
			TradeOffer offer = trader.getTradeOffer(p);
			
			if(offer != null) {
				p.initiateTrade(trader);
				trader.initiateTrade(p);
			}else {
				p.sendMessage("Their trade offer has expired.");
			}
		}else {
			p.sendMessage("Unable to find player.");
		}
	}

}
