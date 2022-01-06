package org.mightykill.rsps.entities.npc;

import java.util.ArrayList;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.skills.Skill;
import org.mightykill.rsps.exchange.offers.GEOffer;

public class NPC extends Entity {
	
	private int npcId;
	private String examine;
	private boolean canAttack = false;
	private int respawnTimer = 0;
	private Position respawnPosition;
	private int[] initialSkillLevel;

	public NPC(int _Id, Position pos, int npcId, int respawn, int[] skillLevels) {
		super(pos);
		this.respawnPosition = pos;
		this._Id = _Id;
		this.npcId = npcId;
		this.initialSkillLevel = skillLevels.clone();
		this.skillLevel = skillLevels.clone();
		this.respawnTimer = respawn;
	}
	
	/*public NPC(int _Id, Position pos, int npcId) {
		this(_Id, pos, npcId, 0, new int[24]);
	}*/
	
	public void setCanAttack(boolean canattack) {
		this.canAttack = canattack;
	}
	
	public boolean canAttack(Entity attacker) {
		return canAttack;
	}

	public void updateMovement() {
		
	}
	
	public int getInitialSkillLevel(int skillId) {
		return this.initialSkillLevel[skillId];
	}

	/*public int getServerId() {
		return 32768+this._Id;
	}*/

	public void process(long curTick) {
		
	}
	
	public void setExamine(String ex) {
		this.examine = ex;
	}
	
	public String getExamine() {
		return this.examine;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	protected void clearFlags() {
		
	}
	
	public int getNPCId() {
		return this.npcId;
	}

	public void updateExchangeOffer(GEOffer offer, boolean notify) {
		//Dont do anything yet
	}

	public void teleport(int x, int y) {
		movement.teleport(new Position(x, y, 0));
	}
	
	public boolean doesRespawn() {
		return respawnTimer>0;
	}

	public Position getRespawnPoint() {
		return respawnPosition;
	}

	protected long getRespawnTime() {
		return respawnTimer;
	}

	public void initiateTrade(Entity tradee) {
		System.out.println("Under construction");
	}

	public boolean isTrading() {
		return false;
	}

}
