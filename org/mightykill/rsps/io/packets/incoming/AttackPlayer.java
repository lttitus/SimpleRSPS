package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;

public class AttackPlayer extends IncomingPacket {
	
	private Player p;

	public AttackPlayer(byte[] data, Player origin) {
		super(160, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int attackId = nextUnsignedShortBigEndian();
		Player attacking = Engine.getPlayer(attackId);
		
		if(attacking != null) {
			p.faceEntity(attacking);
			p.getCombat().setAttacking(attacking);
		}
	}

}
