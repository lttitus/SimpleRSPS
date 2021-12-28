package org.mightykill.rsps.items;

public class WeaponDefinition {
	
	private int itemId;
	private int style;
	private boolean is2h, hasspec;
	private int idleAnim, attackAnim, blockAnim;
	private int hitSound;
	private int cooldown;
	private int styletab;
	
	public WeaponDefinition(int itemid, int style, boolean is2h, boolean hasspec, int idleAnim, int attackAnim, int blockAnim, int hitSound, int cooldown, int styletab) {
		this.itemId = itemid;
		this.style = style;
		this.is2h = is2h;
		this.hasspec = hasspec;
		this.idleAnim = idleAnim;
		this.attackAnim = attackAnim;
		this.blockAnim = blockAnim;
		this.hitSound = hitSound;
		this.cooldown = cooldown;
		this.styletab = styletab;
	}

	public int getStyletab() {
		return styletab;
	}

	public int getItemId() {
		return itemId;
	}

	public int getStyle() {
		return style;
	}

	public boolean isTwoHand() {
		return is2h;
	}
	
	public boolean hasSpecial() {
		return this.hasspec;
	}

	public int getIdleAnim() {
		return idleAnim;
	}

	public int getAttackAnim() {
		return attackAnim;
	}

	public int getBlockAnim() {
		return blockAnim;
	}

	public int getHitSound() {
		return hitSound;
	}
	
	public int getCooldown() {
		return cooldown;
	}

}
