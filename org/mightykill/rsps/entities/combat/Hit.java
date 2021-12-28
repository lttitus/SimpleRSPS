package org.mightykill.rsps.entities.combat;

import org.mightykill.rsps.entities.Entity;

public class Hit {
	
	public static int ZERO = 0;
	public static int HURT = 1;
	public static int POISON = 2;
	public static int DISEASE = 3;
	
	public Entity attacker;
	public int damage;
	public int type;
	
	public Hit(Entity attacker, int damage, int type) {
		this.attacker = attacker;
		this.damage = damage;
		this.type = type;
	}
	
	public Hit(Entity attacker, int damage) {
		this(attacker, damage, (damage>0?HURT:ZERO));
	}
	
	public Hit(int damage, int type) {
		this(null, damage, type);
	}
	
	public Hit(int damage) {
		this(null, damage, (damage>0?HURT:ZERO));
	}

}
