package org.mightykill.rsps.intents;

import java.awt.Point;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.movement.Movement;

public class AttackIntent extends Intent {
	
	protected Entity defender;

	public AttackIntent(Entity attacker, Entity defender) {
		super(attacker);
		this.defender = defender;
	}

	public boolean handleIntent() {
		if(Combat.withinAttackDistance(e, defender)) {	//Allow them to physically hit the defender
			e.faceEntity(defender);
			return true;
		}else {	//Move to them, or out from under them
			Point nearestTile = defender.getClosestPoint(e);
			
			e.getMovement().addStepsToQueue(
					Movement.calculateSteps(
							e.getPosition().getCoords(),
							nearestTile),
					true);
		}
		
		return false;
	}

	public void finishIntent() {
		
	}

}
