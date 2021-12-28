package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.intents.NPCSpeakIntent;

public class NPCSpeak extends IncomingPacket {
	
	private Player p;

	public NPCSpeak(byte[] data, Player origin) {
		super(7, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int worldId = nextUnsignedShortA();
		NPC npc = Engine.getNPC(worldId);
		p.debug(""+worldId);
		p.setIntent(new NPCSpeakIntent(p, npc));
	}

}
