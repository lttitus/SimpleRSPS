package org.mightykill.rsps.actions;

public abstract class TimedAction extends Action {
	
	/** Ticks Till Trigger */
	protected long ttt;
	
	public TimedAction(long created, long ttt) {
		super(created);
		this.ttt = ttt;
	}

	public boolean handleAction(long curTick) {
		if(curTick >= this.createTime+ttt) {
			triggerAction(curTick);
			postAction(curTick);
			return true;
		}
		return false;
	}
	
	public abstract void triggerAction(long curTick);
	
	public abstract void postAction(long curTick);

}
