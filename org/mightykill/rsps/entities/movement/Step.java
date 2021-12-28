package org.mightykill.rsps.entities.movement;

public class Step extends Position {

	public Step(int x, int y, int dir) {
		super(x, y, dir);
	}
	
	public int getDirection() {
		return this.getHeight();
	}

}
