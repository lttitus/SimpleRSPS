package org.mightykill.rsps.intents;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;

public abstract class CoordinateIntent extends Intent {
	
	private Position pos;
	private int dist;

	public CoordinateIntent(Entity e, Position pos, int maxDistance) {
		super(e);
		this.pos = pos;
		this.dist = maxDistance;
	}
	
	public int getDistance() {
		return this.dist;
	}

}
