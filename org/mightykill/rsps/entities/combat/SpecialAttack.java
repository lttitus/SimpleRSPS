package org.mightykill.rsps.entities.combat;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;

public class SpecialAttack {
	
	public static void handleSpecial(Entity e, int weaponId) {
		Entity defender = e.getCombat().getAttacking();
		
		if(defender != null) {
			switch(weaponId) {
			case 5698:	//DDS
				e.setAnimation(1062, 0);
				//TODO: GFX 252
				e.getCombat().hitEntity(defender, (int)(Engine.random.nextFloat()*20));
				e.getCombat().hitEntity(defender, (int)(Engine.random.nextFloat()*15));
				break;
			}
			
			e.getCombat().toggleSpecialAttack();
		}
	}

}
