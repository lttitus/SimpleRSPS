package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.util.Misc;

public class StringInput extends IncomingPacket {
	
	private Player p;

	public StringInput(byte[] data, Player origin) {
		super(42, 8, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		String input = Misc.longToString(nextLong());
		p.debug(input);
	}

}
