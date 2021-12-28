package org.mightykill.rsps.intents;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;

public class NPCSpeakIntent extends PositionIntent {
	
	private NPC npc;

	public NPCSpeakIntent(Entity e, NPC npc) {
		super(e, npc.getPosition(), 1);
		this.npc = npc;
	}

	public void finishIntent() {
		this.e.faceEntity(npc);
		
		switch(npc.getNPCId()) {
		case 44:
		case 6532:
		case 6533:
		case 6534:
		case 6535:	//Bankers
			if(e instanceof Player) {
				((Player)e).sendMessage("This is a Banker");
			}
			break;
		}
	}

}
