package org.mightykill.rsps.entities;

import java.awt.Point;
import java.util.ArrayList;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.actions.Action;
import org.mightykill.rsps.actions.DisappearAction;
import org.mightykill.rsps.actions.RespawnAction;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.combat.Hit;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.entities.trade.TradeOffer;
import org.mightykill.rsps.exchange.offers.GEOffer;
import org.mightykill.rsps.intents.Intent;
import org.mightykill.rsps.io.packets.outgoing.UpdateGEOffer;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Bank;
import org.mightykill.rsps.items.containers.Equipment;
import org.mightykill.rsps.items.containers.Inventory;
import org.mightykill.rsps.util.Misc;
import org.mightykill.rsps.world.regions.Region;
import org.mightykill.rsps.world.zones.Zone;

public abstract class Entity {
	
	/* Server Id */
	protected int _Id;
	protected String name = "!UNDEFINED!";
	
	public boolean updateFaceCoords = false;
	public boolean updateFaceEntity = false;
	public Point facingCoords = null;
	public Entity facingEntity = null;
	public boolean updateAnimation = false;
	public int currentAnimation = -1;
	public boolean appearanceUpdated = false;
	
	protected int[] skillLevel = new int[24];
	protected int[] skillXp = new int[24];
	protected Inventory inventory;
	protected Equipment equipment;
	protected Bank bank;
	protected Combat combat;
	protected int combatLevel = 3;
	protected int[] equipmentBonuses = new int[12];
	
	protected Movement movement = new Movement(this);
	protected ArrayList<Action> actionQueue = new ArrayList<Action>();
	protected Intent currentIntent;
	protected Zone[] zones = new Zone[1];
	
	protected GEOffer[] geOffers = new GEOffer[6];
	protected ArrayList<TradeOffer> tradeOffers = new ArrayList<TradeOffer>();
	
	private boolean visible = true;
	
	public Entity(Position pos) {
		this.movement.setPosition(pos);
		
		for(int i=0;i<24;i++) {
			skillLevel[i] = 1;
			skillXp[i] = 0;
		}
		
		inventory = new Inventory();
		equipment = new Equipment();
		bank = new Bank();
		combat = new Combat(this);
	}
	
	public abstract Position getRespawnPoint();
	
	public void setOffer(int slot, GEOffer offer) {
		geOffers[slot] = offer;
	}
	
	public void removeOffer(int slot) {
		geOffers[slot] = null;
		
		if(this instanceof Player) {
			((Player)this).sendPacket(new UpdateGEOffer(slot, 0, 0, 0, 0, 0, 0));	//Remove the offer from the interface, if we are a Player
		}
	}
	
	public void hide() {
		this.visible = false;
		this.appearanceUpdated = true;
	}
	
	public void show() {
		this.visible = true;
		this.appearanceUpdated = true;
	}
	
	public boolean isHidden() {
		return !this.visible;
	}
	
	/**
	 * Removes an item from the inventory, then creates an item on the ground;<br>
	 * Remember to refresh the inventory if done to a Player
	 * @param slot
	 */
	public void dropItem(int slot) {
		Item item = inventory.getItemInSlot(slot);
		
		if(item != null) {
			Position pos = getPosition();
			Engine.groundItems.createGroundItem(item.getItemId(), item.getItemAmount(), pos.x, pos.y, this);
			inventory.setItem(slot, null);	//Player needs to refresh; done in the packet
		}
	}
	
	public abstract void updateExchangeOffer(GEOffer offer, boolean notify);
	
	public int giveItem(Item item) {
		int given = inventory.addAmount(item, false);
		if(this instanceof Player) {
			((Player)this).refreshInventory();
		}
		return given;
	}
	
	public int giveItem(int itemId, int amount) {
		return giveItem(new Item(itemId, amount));
	}
	
	public Region getCurrentRegion() {
		return this.movement.getCurrentRegion();
	}
	
	public Point getCurrentRegionPoint() {
		return this.movement.getCurrentRegionPoint();
	}
	
	public abstract void initiateTrade(Entity tradee);
	public abstract boolean isTrading();
	
	public void processEntity(long curTick) {
		if(!living) {
			if(skillLevel[Skill.HITPOINTS] > 0) living = true;	//Fix us up if we shouldn't be dead...
		}else {
			if(curTick % 50 == 0) {	//Every 30 seconds
				if(this.skillLevel[Skill.HITPOINTS] < this.getLevelForXP(Skill.HITPOINTS)) {	//Heal the Entity; should be first priority
					heal(1);
				}
			}
		}
		
		zones = Engine.zones.getApplicableZones(this).toArray(zones);
		
		/* Age trade offers, removing if any are > 1 minute old */
		TradeOffer[] offers = new TradeOffer[tradeOffers.size()];
		offers = tradeOffers.toArray(offers);
		for(TradeOffer offer:offers) {
			if(offer.age() <= 0) {
				tradeOffers.remove(offer);
			}
		}
		
		Action[] actionList = new Action[actionQueue.size()];
		actionList = actionQueue.toArray(actionList);
		for(Action action:actionList) {
			if(action.handleAction(curTick)) {
				actionQueue.remove(action);
			}
		}
		
		if(currentIntent != null) {
			if(currentIntent.handleIntent()) {
				currentIntent.finishIntent();
				currentIntent = null;
			}
		}
		
		if(movement.getEnergy() < 0x7FF) {	//Max energy
			movement.setRunEnergy(movement.getEnergy()+1);
		}
		
		process(curTick);
		
		movement.progress(this);	//FIXME
		combat.process(curTick);
	}
	
	public void walkTo(Point p) {
		movement.addStepsToQueue(Movement.calculateSteps(movement.getPosition().getCoords(), p), true);
	}
	
	public ArrayList<Zone> getZones() {
		return Engine.zones.getApplicableZones(this);
	}
	
	public Region getRegion() {
		return this.movement.getCurrentRegion();
	}
	
	/**
	 * Loops through the current Zones; if we are in one, return true
	 * @return True if any of our Zones are multicombat
	 */
	public boolean isInMulticombat() {
		for(Zone z:zones) {
			if(z != null) {
				if(z.isMulti()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Heal the Entity
	 * @param hp The amount of HP to heal
	 * @param overheal If we can heal more than 100% of our health (Sara brews, etc)
	 */
	public void heal(int hp, boolean overheal) {
		int diff = this.getLevelForXP(Skill.HITPOINTS)-this.skillLevel[Skill.HITPOINTS];
		
		if(diff < hp && !overheal) hp = diff;	//If we are healing more health than what we have. E.g. heal 25 when we only lost 18, set it to 18
		this.setLevel(Skill.HITPOINTS, this.skillLevel[Skill.HITPOINTS]+hp);
	}
	
	public void heal(int hp) {
		heal(hp, false);
	}
	
	public void setIntent(Intent intent) {
		this.currentIntent = intent;
	}
	
	public void queueAction(Action action) {
		this.actionQueue.add(action);
	}
	
	public void setAnimation(int animId, int delay) {
		this.updateAnimation = true;
		this.currentAnimation = animId;
		this.appearanceUpdated = true;
	}
	
	public Combat getCombat() {
		return this.combat;
	}
	
	public void faceEntity(Entity e) {
		this.facingEntity = e;
		this.updateFaceEntity = true;
		this.appearanceUpdated = true;
	}
	
	public void faceCoords(Point coords) {
		this.facingCoords = coords;
		this.updateFaceCoords = true;
		this.appearanceUpdated = true;
	}
	
	public void setLevel(int skillId, int level) {
		this.skillLevel[skillId] = level;
	}
	
	public void setLevelByXP(int skillId, int xp) {
		this.skillXp[skillId] = xp;
		this.setLevel(skillId, getLevelForXP(skillId));
	}
	
	protected abstract void process(long curTick);
	
	private boolean living = true;
	public boolean isAlive() {
		return living;
	}
	
	public abstract boolean canAttack(Entity attacker);
	
	/**
	 * Checks to see if this Entity is under attack from an Entity other than the one that is passed.
	 * @param test
	 * @return 
	 */
	public boolean isUnderAttack(Entity test) {
		ArrayList<Entity> attackers = combat.getAttackers();
		
		if(attackers.size() > 0) {
			if(attackers.contains(test)) {
				return false;
			}
		}else {
			return false;
		}
		
		return true;	//Default to true so we can't abuse some bug, hopefully
	}
	
	/**
	 * Gets the tile just outside of this Entity.<br>
	 * FIXME: Add a better way of doing this
	 * @param other
	 * @return
	 */
	public Point getClosestPoint(Entity other) {
		int dx = 0, dy = 0;
		Point otherPos = other.getPosition().getCoords();
		Point thisPos = this.getPosition().getCoords();
		
		if(otherPos.x > thisPos.x) {
			dx++;
		}else {
			dx--;
		}
		
		if(otherPos.y > thisPos.y) {
			dx++;
		}else if(otherPos.y < thisPos.y) {
			dx--;
		}
		
		return new Point(thisPos.x+dx, thisPos.y+dy);
	}
	
	private int deathAnim = 7185;
	public void die(long deathTick) {
		if(living) {	//TODO: Overhaul combat and death mechanics
			living = false;
			this.combat.setAttacking(null);
			this.setAnimation(deathAnim, 0);
			this.queueAction(new DisappearAction(this, deathTick, 5));
			this.queueAction(new RespawnAction(this, deathTick, 5+getRespawnTime()));
		}
	}
	
	protected abstract long getRespawnTime();
	
	public Movement getMovement() {
		return this.movement;
	}
	
	public Position getPosition() {
		return this.movement.getPosition();
	}
	
	public void damage(Entity attacker, int damage, int type, boolean playAnim) {
		if(damage > skillLevel[Skill.HITPOINTS]) {
			damage = skillLevel[Skill.HITPOINTS];
		}
		this.combat.applyHit(
				new Hit(attacker, damage, type));
		this.setLevel(Skill.HITPOINTS, skillLevel[Skill.HITPOINTS]-damage);
		if(playAnim) setAnimation(combat.getBlockAnim(), 0);
	}
	
	public void damage(Entity attacker, int damage, boolean playAnim) {
		this.damage(attacker, damage, (damage>0?Hit.HURT:Hit.ZERO), playAnim);
	}
	
	public void damage(int damage) {
		this.damage(null, damage, false);
	}
	
	public int getLevelForXP(int skillId) {
        int exp = skillXp[skillId];
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= 100; lvl++) {
            points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
            output = (int) Math.floor(points / 4);
            if ((output - 1) >= exp) {
                return lvl;
            }
            if(output > 99) {
            	output = 99;
            }
        }
        return output;
    }
	
	public int[] getAllXP() {
		return this.skillXp;
	}
	
	public int[] getAllLevels() {
		return this.skillLevel;
	}
	
	/**
	 * Resets all flags used in the update process
	 */
	public void resetFlags() {
		appearanceUpdated = false;
		movement.teleported = false;
		movement.walkDir = movement.runDir = -1;
		movement.teleported = false;
		facingEntity = null;
		facingCoords = null;
		updateFaceCoords = false;
		updateFaceEntity = false;
		if(currentAnimation != -1) {	//Stop animation from looping
			currentAnimation = -1;
			updateAnimation = true;
		}
		this.combat.clearDamageThisTick();
		clearFlags();
	}
	
	public abstract void teleport(int x, int y);
	
	public void teleport(Position pos) {
		teleport(pos.x, pos.y);
	}
	
	protected abstract void clearFlags();
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public Equipment getEquipment() {
		return this.equipment;
	}
	
	public int getSkillLevel(int skillId) {
		return this.skillLevel[skillId];
	}
	
	public int getSkillXp(int skillId) {
		return this.skillXp[skillId];
	}
	
	public int getWorldIndex() {
		return this._Id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCombatLevel() {
		return this.combatLevel;
	}

}
