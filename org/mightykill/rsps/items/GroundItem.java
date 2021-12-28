package org.mightykill.rsps.items;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;

public class GroundItem extends Item {
	
	private int x, y;
	private Entity owner;
	private int initRespawn = 0;
	private int respawnTimer = 0;
	private int publicTimer = 0;
	
	public GroundItem(int itemId, int itemAmount, int x, int y, Entity owner, int respawnTimer) {
		super(itemId, itemAmount);
		this.initRespawn = respawnTimer;
		this.x = x;
		this.y = y;
		this.owner = owner;
	}

	public GroundItem(int itemId, int itemAmount, int x, int y, Entity owner) {
		this(itemId, itemAmount, x, y, owner, 0);
	}
	
	public GroundItem(int itemId, int itemAmount, int x, int y) {
		this(itemId, itemAmount, x, y, null);
	}
	
	public void process(long currTick) {
		publicTimer++;
	}
	
	public Position getPosition() {
		return new Position(x, y, 0);
	}
	
	public Entity getOwner() {
		return this.owner;
	}
	
	public int getPublicTimer() {
		return this.publicTimer;
	}
	
	public void resetTimer() {
		this.publicTimer = 0;
	}
	
	public void makePublic() {
		this.owner = null;
	}
	
	public boolean isPublic() {
		return this.owner == null;
	}

}
