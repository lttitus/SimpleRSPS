package org.mightykill.rsps.entities.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;

public class NPCManager {
	
	private Connection sql;
	
	private HashMap<Integer, CachedNPC> cachedNPCDefinitions = new HashMap<Integer, CachedNPC>();
	//private ArrayList<NPC> npcList = new ArrayList<NPC>();
	private NPC[] npcList = new NPC[32768];
	
	public NPCManager(Connection sqlConnection) {
		this.sql = sqlConnection;
		System.out.println("Loaded "+loadNPCDefinitions()+" NPC Definitions");
		System.out.println("Spawned "+loadNPCs()+" NPCs");
	}
	
	public int loadNPCDefinitions() {
		int loaded = 0;
		
		try {
			PreparedStatement ps = sql.prepareStatement("SELECT * FROM npc_definitions");
			
			if(ps.execute()) {
				ResultSet results = ps.getResultSet();
				
				while(results.next()) {
					int npcId = results.getInt("npcid");
					String name = results.getString("name");
					String examine = results.getString("examine");
					String rawSkills = results.getString("skills");
					int skillLevels[] = new int[24];
					for(int skillId=0;skillId<24;skillId++) {
						int skillLevel = Integer.parseInt(rawSkills.substring(skillId*2, skillId*2+2), 16);
						
						skillLevels[skillId] = skillLevel;
					}
					String rawBonuses = results.getString("bonuses");
					int bonuses[] = new int[12];
					for(int bonusId=0;bonusId<12;bonusId++) {
						int bonus = Integer.parseInt(rawBonuses.substring(bonusId*2, bonusId*2+2), 16);
						
						bonuses[bonusId] = bonus;
					}
					boolean canAttack = results.getBoolean("canattack");
					int attackAnim = results.getInt("attackanim");
					int blockAnim = results.getInt("blockanim");
					int respawn = results.getInt("respawn");
					
					cachedNPCDefinitions.put(npcId, new CachedNPC(npcId, name, examine, skillLevels, bonuses, canAttack, attackAnim, blockAnim, respawn));
					
					loaded++;
				}
				
			}
		} catch(SQLException e) {
			
		}
		
		return loaded;
	}
	
	public int loadNPCs() {
		int loaded = 0;
		
		try {
			PreparedStatement ps = sql.prepareStatement("SELECT * FROM npc_spawns");
			
			if(ps.execute()) {
				npcList = new NPC[32768];
				//npcList.clear();
				ResultSet results = ps.getResultSet();
				
				while(results.next()) {
					int worldId = results.getInt("worldid");
					int npcId = results.getInt("npcid");
					int spawnx = results.getInt("spawnx");
					int spawny = results.getInt("spawny");
					int spawnh = results.getInt("spawnh");
					int facedir = results.getInt("facedir");
					
					CachedNPC npcdef = cachedNPCDefinitions.get(npcId);
					if(npcdef != null) {
						Position pos = new Position(spawnx, spawny, spawnh);
						NPC created = createNPC(worldId, npcdef, pos);
						
						if(created != null) {
							created.getMovement().setDirection(facedir);
							loaded++;
						}else {
							System.err.println("Unable to create NPC "+npcId);
						}
					}else {
						System.err.println("No NPC Definition found for "+npcId);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return loaded;
	}
	
	public ArrayList<NPC> getNPCsInArea(Entity e) {
		ArrayList<NPC> npcs = new ArrayList<NPC>();
		Position ePos = e.getPosition();
		
		for(NPC n:npcList) {
			if(n != null) {
				Position nPos = n.getPosition();
				
				if(nPos.x >= ePos.x-15 && nPos.x <= ePos.x+15 &&
					nPos.y >= ePos.y-15 && nPos.y <= ePos.y+15) {
					npcs.add(n);
				}
			}
		}
		
		return npcs;
	}
	
	public NPC[]/*ArrayList<NPC>*/ getNPCList() {
		return this.npcList;
	}
	
	public NPC getNPC(int worldId) {
		return this.npcList[worldId];
		//return this.npcList.get(id);
	}
	
	private int getNextFreeSlot() {
		for(int slot=0;slot<npcList.length;slot++) {
			if(npcList[slot] == null) return slot;
		}
		
		return -1;
	}
	
	public NPC createNPC(int worldId, CachedNPC npcdefs, Position pos) {
		if(worldId != -1) {
			NPC newNPC = npcdefs.createNPC(worldId, pos);
			
			npcList[worldId] = newNPC;
			return newNPC;
		}else {
			System.err.println("NPC List is full!");
		}

		return null;
	}
	
	public NPC createNPC(CachedNPC npcdefs, Position pos) {
		return createNPC(getNextFreeSlot(), npcdefs, pos);
	}
	
	public boolean spawnNPC(int npcId, Position pos) {
		CachedNPC npcdef = cachedNPCDefinitions.get(npcId);
		
		if(npcdef != null) {
			return createNPC(npcdef, pos) != null;
		}
		
		return false;
	}

	public CachedNPC getCachedNPC(int npcId) {
		return cachedNPCDefinitions.get(npcId);
	}

}
