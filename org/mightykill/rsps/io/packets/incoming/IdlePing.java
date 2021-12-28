package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;

public class IdlePing extends IncomingPacket {
	
	private Player p;

	public IdlePing(Player origin) {
		super(47, 0, null, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		p.idleCount++;
	}

}
