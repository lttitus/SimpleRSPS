package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;

public class ExamineItem extends IncomingPacket {
	
	private Player p;

	public ExamineItem(int packetId, byte[] data, Player origin) {
		super(packetId, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int itemId = nextUnsignedShortBigEndianA();
		
		p.sendMessage(Engine.items.getDefinition(itemId).getExamine());
	}

}
