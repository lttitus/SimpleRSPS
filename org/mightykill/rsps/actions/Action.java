package org.mightykill.rsps.actions;

public abstract class Action {
	
	protected long createTime;
	
	public Action(long created) {
		this.createTime = created;
	}
	
	/**
	 * Processes this Action
	 * @param curTick The tick the Action was checked on
	 * @return True if it was completed, False otherwise
	 */
	public abstract boolean handleAction(long curTick);
	
	public abstract void postAction(long curTick);

}
