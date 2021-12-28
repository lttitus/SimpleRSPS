package org.mightykill.rsps.entities.combat;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.intents.AttackIntent;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Equipment;
import org.mightykill.rsps.util.Misc;

public class Combat {
	
	public static final int DEFAULT_ATTACK = 422;
	
	private Entity origin;
	private int attackAnim = 422;
	private int blockAnim = 424;
	private ArrayList<Hit> damageTakenThisTick = new ArrayList<Hit>();
	private HashMap<Entity, Integer> totalDamageTaken = new HashMap<Entity, Integer>();
	private ArrayList<Entity> attackers = new ArrayList<Entity>();
	private boolean autoRetaliate = true;
	private boolean willSpec = false;
	
	private Entity defender = null;
	private int cooldown = 0;
	
	public Combat(Entity e) {
		this.origin = e;
	}
	
	public void setAttackAnim(int anim) {
		this.attackAnim = anim;
	}
	
	public void setBlockAnim(int anim) {
		this.blockAnim = anim;
	}
	
	public void process(long curTick) {
		if(origin.getSkillLevel(Skill.HITPOINTS) > 0 && origin.isAlive()) {	//We have not died on this tick and we are currently not dying
			if(cooldown > 0) {	//Ticks before we can attack again
				cooldown--;
				return;
			}
			
			if(defender != null) {
				if(defender.isAlive()) {
					if(willAttack()) {
						origin.faceEntity(defender);
						if(defender.canAttack(origin)) {
							if(!withinAttackDistance(origin, defender)) {
								origin.setIntent(new AttackIntent(origin, defender));
							}else {
								int damage = 0;
								if(Engine.random.nextDouble() <= hitChance(origin, defender, 0)) {
									if(!willSpec) {
										damage = (int)Math.ceil(getMaxHit(origin, 0)*Engine.random.nextFloat());
										//int damage = (int)(Engine.random.nextFloat()*20);
										
										
									}else {	//Special attacks
										SpecialAttack.handleSpecial(origin, origin.getEquipment().getItemInSlot(Equipment.WEAPON).getItemId());
									}
								}
								
								hitEntity(defender, damage);
							}
						}else {
							origin.getCombat().setAttacking(null);
							
							if(origin instanceof Player) {
								((Player)origin).sendMessage("You cannot attack them here.");
							}
						}
					}else {
						origin.getCombat().setAttacking(null);
						
						if(origin instanceof Player) {
							((Player)origin).sendMessage("They are already under attack!");
						}
					}
				}else {
					setAttacking(null);
				}
			}else {
				setAttacking(null);
			}
		}else {
			origin.die(curTick);
		}
	}
	
	/**
	 * Calculates the hitchance of the attacking Player, p
	 * @param p The attacking Player
	 * @param p2 The defending Player
	 * @param type The attack style being used. 0-Melee, 1-Range, 2-Mage
	 * @return The hit chance, in a percent
	 */
	public static double hitChance(Entity attacker, Entity defender, int type) {
		double effectiveStrength = 0;	//The attacking Player's effective Strength
		double effectiveDefence = (defender.getSkillLevel(Skill.DEFENCE)/**Prayer.getPrayerBonus(defender, type)*/)+8;
		
		if(type == 0) {
			effectiveStrength = (attacker.getSkillLevel(Skill.STRENGTH)/**Prayer.getPrayerBonus(attacker, type)*/)+8;
		}
		
		double hitChance = effectiveStrength/**(1+attacker.getAttackBonus()/64)*/;
		double blockChance = effectiveDefence/**(1+defender.getDefenceBonus()/64)*/;
		
		if(hitChance < blockChance) {
			return (hitChance - 1) / (1.8 * blockChance);
		} else {
			return 1 - (blockChance + 1) / (1.8 * hitChance);
		}
	}
	
	/**
	 * Gets the max hit of an Entity<br>
	 * <p>Refer to <b>"http://services.runescape.com/m=rswiki/en/Maximum_Hit_Formula"</b><br>
	 * for more information</p>
	 * @param e The Entity you want to test
	 * @return The max hit of this Entity
	 */
	public static int getMaxHit(Entity e, int attackStyle) {
		int maxDamage = 0;
		switch(attackStyle) {	
		case 0:	//Melee
			//e.sendMessage("Melee");
			int strengthBonus = 0;//e.equipmentBonus[10];	//The Strength bonus your equipment gives you
			double effectiveStrength = (e.getSkillLevel(Skill.STRENGTH)/**Prayer.getPrayerBonus(e, 1)*/)+8;	//The base Strength bonus
			maxDamage = (int)Math.floor(0.5+effectiveStrength*(strengthBonus+64)/640);	//TODO Add combat style bonuses
			System.out.println(e.getName()+" max hit is "+maxDamage);
			break;
		case 1:	//Range
			//e.sendMessage("Range");
			break;
		case 2:	//Magic
			//e.sendMessage("Magic");
			maxDamage = 20;
			break;
	}
		
		return maxDamage;
	}
	
	public void toggleSpecialAttack() {
		this.willSpec = !willSpec;
	}
	
	public int getAttackAnim() {
		return attackAnim;
	}
	
	public int getBlockAnim() {
		return blockAnim;
	}
	
	private boolean willAttack() {
		return (defender != null && 
				(defender.isUnderAttack(origin) && defender.isInMulticombat() ||
						!defender.isUnderAttack(origin)) &&
				((defender instanceof Player && ((Player)defender).getClient().isLoggedIn()) ||
						!(defender instanceof Player)));
	}
	
	/**
	 * Returns the distance this Entity can attack based on their weapon/attack style
	 * @return
	 */
	public int getAttackDistance() {
		return 1;
	}
	
	public void hitEntity(Entity defender, int damage) {
		if(defender.isAlive()) {
			origin.faceEntity(defender);
			origin.setAnimation(attackAnim, 0);
			
			if(origin instanceof Player) {
				Item weapon = origin.getEquipment().getItemInSlot(Equipment.WEAPON);
				
				if(weapon != null) {
					((Player)origin).playSound(
						Engine.items.getHandhelds(
								weapon.getItemId()).getHitSound());
					
					cooldown = Engine.items.getHandhelds(weapon.getItemId()).getCooldown();
				}else {
					((Player)origin).playSound(511);
					
					cooldown = 4;
				}
				
			}else {	//NPC
				cooldown = 5;
			}
			
			defender.damage(origin, damage, true);
			defender.getCombat().addAttacker(origin);
		}
	}
	
	public void addAttacker(Entity attacker) throws ConcurrentModificationException {
		if(!attackers.contains(attacker)) {
			if(attackers.add(attacker)) {
				//System.out.println("Added "+attacker.getName());
			}
		}
	}
	
	public void removeAttacker(Entity attacker) throws ConcurrentModificationException {
		if(attackers.remove(attacker)) {
			//System.out.println("Removed "+attacker.getName());
		}
	}
	
	public ArrayList<Entity> getAttackers() {
		return this.attackers;
	}
	
	public void setAttacking(Entity defender) {
		//this.cooldown += 1;
		try {
			if(defender == null && this.defender != null) {
				origin.faceEntity(null);
				this.defender.getCombat().removeAttacker(origin);
			}
			this.defender = defender;
			
			if(willAttack()) {
				if(defender != null) {
					defender.getCombat().setCooldown(1);	//'shock' time
					defender.getCombat().addAttacker(origin);
				}
			}
		}catch(ConcurrentModificationException cme) {
			System.err.println("Error adding/removing attacker");
		}
	}
	
	public void setCooldown(int ticks) {
		this.cooldown = ticks;
	}
	
	public int getCooldown() {
		return this.cooldown;
	}
	
	public void applyHit(Hit hit) {
		this.damageTakenThisTick.add(hit);
		
		if(hit.attacker == null) {
			hit.attacker = origin;
		}else {
			if(this.autoRetaliate && this.defender == null) {	//If we are not attacking anyone, and we will if we are hit, set them as the main defender. If we are already fighting, focus on them
				this.setAttacking(hit.attacker);
			}
		}
		
		Integer damage = this.totalDamageTaken.get(hit.attacker);
		if(damage == null) damage = 0;
		this.totalDamageTaken.put(hit.attacker, damage+hit.damage);
	}
	
	public boolean isInCombat() {
		return this.defender != null || !this.attackers.isEmpty();	//TODO: Re-do this so it takes into account the last hit time, rather than not being attacked at all
	}
	
	public void removeTickDamage(int index) {
		this.damageTakenThisTick.remove(index);
	}
	
	public void clearDamageThisTick() {
		int hitsTaken = damageTakenThisTick.size();
		if(hitsTaken > 0) {
			if(hitsTaken > 1) {
				removeTickDamage(1);
			}
			removeTickDamage(0);
		}
	}
	
	public ArrayList<Hit> getDamageThisTick() {
		return this.damageTakenThisTick;
	}
	
	public Entity getHighestHitter() {
		Entity highestHitter = null;
		int mostDamageDealt = 0;
		
		for(Iterator<Map.Entry<Entity, Integer>> it=totalDamageTaken.entrySet().iterator();it.hasNext();) {
			Map.Entry<Entity, Integer> e = it.next();
			if(e.getValue() > mostDamageDealt) {
				highestHitter = e.getKey();
				mostDamageDealt = e.getValue();
			}
		}
		
		return highestHitter;
	}
	
	public void setRetaliate(boolean ret) {
		this.autoRetaliate = ret;
	}
	
	public boolean willRetaliate() {
		return this.autoRetaliate;
	}
	
	public Entity getAttacking() {
		return this.defender;
	}
	
	/**
	 * This is the calculation used by Jagex, taken from: "http://runescape.wikia.com/wiki/Combat_level"
	 * @param p
	 */
	public static int calculateCombat(Player p) {
        int attack = p.getLevelForXP(0);
        int defence = p.getLevelForXP(1);
        int strength = p.getLevelForXP(2);
        int hp = p.getLevelForXP(3);
        int prayer = p.getLevelForXP(5);
        int ranged = p.getLevelForXP(4);
        int magic = p.getLevelForXP(6);
		int summoning = p.getLevelForXP(23);
		
		return (int)((1.3*Misc.max(attack+strength, 
									2*magic,
									2*ranged)+
        				(defence+hp+(0.5*prayer)+(0.5*summoning))
        			)*0.25);
    }
	
	public static boolean withinAttackDistance(Entity attacker, Entity defender) {
		Position attPos = attacker.getPosition();
		Position defPos = defender.getPosition();
		int attackDistance = attacker.getCombat().getAttackDistance();
		int distance = Misc.getDistance(attPos.x, attPos.y, defPos.x, defPos.y);
		
		return distance <= attackDistance &&
				distance > 0;
	}

}
