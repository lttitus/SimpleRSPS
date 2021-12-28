package org.mightykill.rsps.intents;

import java.awt.Point;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.movement.Movement;

public class FollowIntent extends Intent {
	
	protected Entity followee;

	public FollowIntent(Entity e, Entity followee) {
		super(e);
		this.followee = followee;
	}

	public boolean handleIntent() {
		if(!Combat.withinAttackDistance(e, followee)) {
			Point nearestTile = followee.getClosestPoint(e);
			
			e.getMovement().addStepsToQueue(
					Movement.calculateSteps(
							e.getPosition().getCoords(),
							nearestTile),
					true);
		}
		
		return false;	//Continue following until we aren't
	}

	public void finishIntent() {
		//Should never finish...
	}

}
