package org.mightykill.rsps.actions;

import org.mightykill.rsps.entities.Entity;

public class AppearAction extends TimedAction {
	
	private Entity e;

	public AppearAction(Entity affected, long created, long ttt) {
		super(created, ttt);
		this.e = affected;
	}

	public void triggerAction(long curTick) {
		e.show();
	}

	public void postAction(long curTick) {
		
	}

}
