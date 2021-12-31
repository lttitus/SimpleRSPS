package org.mightykill.rsps.actions;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;

public class RespawnAction extends TimedAction {
	
	private Entity respawning;

	public RespawnAction(Entity e, long created, long respawnTimer) {
		super(created, respawnTimer);
		this.respawning = e;
	}

	public void triggerAction(long curTick) {
		//TODO: Drop items
		if(respawning instanceof Player) {
			respawning.teleport(Engine.HOME/*, respawning*/);
			respawning.setLevel(Skill.HITPOINTS, respawning.getLevelForXP(Skill.HITPOINTS));
			((Player) respawning).sendMessage("Oh dear you are dead!");
		}else {
			NPC n = (NPC)respawning;
			if(n.doesRespawn()) {
				respawning.teleport(n.getRespawnPoint());
				n.setLevel(Skill.HITPOINTS, n.getInitialSkillLevel(Skill.HITPOINTS));
				
			}
		}
		
		respawning.show();
	}

	public void postAction(long curTick) {
		//respawning.setAnimation(-1, 0);
		
		//respawning.appearanceUpdated = true;
	}

}
