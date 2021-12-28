package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;

public class ClosedInterface extends IncomingPacket {
	
	private Player p;

	public ClosedInterface(byte[] data, Player origin) {
		super(108, 0, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		p.closeInterface();
	}

}
