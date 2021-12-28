package org.mightykill.rsps.actions;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Hit;

public class PoisonAction extends DamageAction {

	public PoisonAction(Entity attacker, Entity defender, long created, int damage) {
		super(attacker, defender, created, 15, damage, Hit.POISON, false);
	}
	
	public void triggerAction(long curTick) {
		super.triggerAction(curTick);
		
	}

	public void postAction(long curTick) {
		if(damage > 1) defender.queueAction(
				new PoisonAction(
						attacker, 
						defender, 
						curTick,
						damage-1));	//Renew this action every 15 ticks, until we are done doing damage
	}

}
