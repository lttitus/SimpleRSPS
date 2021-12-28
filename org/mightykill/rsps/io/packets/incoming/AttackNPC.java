package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;

public class AttackNPC extends IncomingPacket {
	
	private Player p;

	public AttackNPC(byte[] data, Player origin) {
		super(123, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int npcId = nextUnsignedShort();
		NPC defender = Engine.npcs.getNPC(npcId);
		p.sendMessage("Attacking: "+defender.getName()+"; "+npcId);
		p.getCombat().setAttacking(defender);
	}

}
