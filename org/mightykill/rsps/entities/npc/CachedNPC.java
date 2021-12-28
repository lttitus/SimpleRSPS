package org.mightykill.rsps.entities.npc;

import org.mightykill.rsps.entities.movement.Position;

public class CachedNPC {
	
	private int npcId;
	private String name;
	private String examine;
	private int[] skills;
	private int[] bonuses;
	private boolean canAttack;
	private int attackAnim;
	private int blockAnim;
	private int respawn;
	
	public CachedNPC(int npcId, String name, String examine, int[] skills, int[] bonuses, boolean canAttack, int attackAnim, int blockAnim, int respawn) {
		this.npcId = npcId;
		this.name = name;
		this.examine = examine;
		this.skills = skills;
		this.bonuses = bonuses;
		this.canAttack = canAttack;
		this.attackAnim = attackAnim;
		this.blockAnim = blockAnim;
		this.respawn = respawn;
	}
	
	public NPC createNPC(int worldId, Position pos) {
		NPC newNPC = new NPC(worldId, pos, npcId, respawn, skills);
		newNPC.setName(name);
		newNPC.setCanAttack(canAttack);
		newNPC.getCombat().setAttackAnim(attackAnim);
		newNPC.getCombat().setBlockAnim(blockAnim);
		newNPC.setExamine(examine);
		return newNPC;
	}
	
	public String getExamine() {
		return this.examine;
	}

}
