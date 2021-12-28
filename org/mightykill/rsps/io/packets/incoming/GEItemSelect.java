package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.GrandExchange;

public class GEItemSelect extends IncomingPacket {
	
	private Player p;

	public GEItemSelect(byte[] data, Player origin) {
		super(195, 2, data, origin.getClient());
		this.p = origin;
		
	}

	public void handlePacket() {
		int itemId = nextUnsignedShort();
		p.closeChatboxInterface();
		GrandExchange.updateOfferScreen(p, itemId);
	}

}
