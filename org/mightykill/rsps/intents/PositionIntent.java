package org.mightykill.rsps.intents;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.util.Misc;

public abstract class PositionIntent extends Intent {
	
	private Position pos;
	private int distance;

	public PositionIntent(Entity e, Position pos, int distance) {
		super(e);
		
		this.pos = pos;
		this.distance = distance;
	}

	public boolean handleIntent() {
		if(Misc.getDistance(e.getPosition().x, e.getPosition().y, pos.x, pos.y) <= this.distance) {
			return true;
		}
		return false;
	}

	public abstract void finishIntent();

}
