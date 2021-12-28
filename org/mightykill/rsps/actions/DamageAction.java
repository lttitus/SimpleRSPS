package org.mightykill.rsps.actions;

import org.mightykill.rsps.entities.Entity;

public abstract class DamageAction extends TimedAction {
	
	protected Entity attacker, defender;
	protected int damage, type;
	protected boolean blockAnim;

	public DamageAction(Entity attacker, Entity defender, long created, long ttt, int damage, int type, boolean anim) {
		super(created, ttt);
		this.attacker = attacker;
		this.defender = defender;
		this.damage = damage;
		this.type = type;
		this.blockAnim = anim;
	}

	public void triggerAction(long curTick) {
		this.defender.damage(attacker, damage, type, blockAnim);
	}

	public abstract void postAction(long curTick);

}
