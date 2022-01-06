package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.client.Client;

public class ItemSelect extends IncomingPacket {
	
	private Player p;

	public ItemSelect(byte[] data, Player origin) {
		super(220, 8, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int junk1 = nextUnsignedByte();
		int interfaceId = nextUnsignedShort();
		int junk2 = nextUnsignedByte();
		int itemId = nextUnsignedShortBigEndian();
		int slot = nextUnsignedShortA();
		
		p.debug(junk1+" "+interfaceId+" "+junk2+" "+itemId+" "+slot);
	}

}
