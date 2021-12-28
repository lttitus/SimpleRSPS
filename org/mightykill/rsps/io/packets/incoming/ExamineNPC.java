package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.npc.CachedNPC;
import org.mightykill.rsps.entities.player.Player;

public class ExamineNPC extends IncomingPacket {
	
	private Player p;

	public ExamineNPC(byte[] data, Player origin) {
		super(88, 2, data, origin.getClient());
		
		this.p = origin;
	}

	public void handlePacket() {
		int npcId = nextUnsignedShort();
		
		CachedNPC npc = Engine.npcs.getCachedNPC(npcId);
		p.sendMessage(npc.getExamine());
	}

}
