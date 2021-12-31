package org.mightykill.rsps.actions;

import org.mightykill.rsps.entities.Entity;

public class DisappearAction extends TimedAction {
	
	private Entity e;

	public DisappearAction(Entity affected, long created, long ttt) {
		super(created, ttt);
		this.e = affected;
	}

	public void triggerAction(long curTick) {
		e.hide();
	}

	public void postAction(long curTick) {
		
	}

}
