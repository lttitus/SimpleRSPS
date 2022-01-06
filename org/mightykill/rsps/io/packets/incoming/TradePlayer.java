package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.trade.TradeOffer;

public class TradePlayer extends IncomingPacket {
	
	private Player p;

	public TradePlayer(byte[] data, Player origin) {
		super(227, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int playerWorldId = nextUnsignedShortBigEndianA();
		
		Player other = Engine.players.getPlayer(playerWorldId);
		if(other != null) {
			TradeOffer existingOffer = other.getTradeOffer(p);
			
			if(existingOffer == null) {
				p.addTradeOffer(new TradeOffer(other));
				other.sendMessage(p.getName()+":tradereq:");
			}else {
				p.initiateTrade(other);
				other.initiateTrade(p);
			}
		}
	}

}
