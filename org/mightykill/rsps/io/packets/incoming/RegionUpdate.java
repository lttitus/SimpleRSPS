package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.util.Misc;

public class RegionUpdate extends IncomingPacket {
	
	private Player p;

	public RegionUpdate(byte[] data, Player origin) {
		super(22, 5, data, origin.getClient());
		
		this.p = origin;
	}

	public void handlePacket() {
		//System.out.println("Region data: "+Misc.getHexDump(data));
	}

}
