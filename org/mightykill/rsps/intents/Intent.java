package org.mightykill.rsps.intents;

import org.mightykill.rsps.entities.Entity;

public abstract class Intent {
	
	protected Entity e;
	
	public Intent(Entity e) {
		this.e = e;
	}
	
	public abstract boolean handleIntent();
	
	public abstract void finishIntent();

}
