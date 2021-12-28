package org.mightykill.rsps.world.regions;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;

/**
 * Contains Region data for the World
 * @author Green
 *
 */
public class RegionManager {
	
	private Connection sql;
	private Region[][] regions;
	
	public RegionManager(Connection sqlConnection, int xRegions, int yRegions) {
		this.sql = sqlConnection;
		
		regions = new Region[xRegions][yRegions];
		for(int w=0;w<xRegions;w++) {
			for(int h=0;h<yRegions;h++) {
				regions[w][h] = new Region(w, h, "Region not set");
			}
		}
		
		System.out.println("Loaded "+loadRegions()+" regions.");
	}
	
	public int loadRegions() {
		int regionCount = 0;
		
		try {
			PreparedStatement ps = sql.prepareStatement("SELECT * FROM regions");
			
			if(ps.execute()) {
				ResultSet results = ps.getResultSet();
				
				while(results.next()) {
					int regionId = results.getInt("id");
					
					int bx1 = results.getInt("boundx1");	//Bottom-left
					int by1 = results.getInt("boundy1");
					int bx2 = results.getInt("boundx2");	//Top-right
					int by2 = results.getInt("boundy2");
					
					String description = results.getString("description");
					int musicId = results.getInt("musicid");
					
					for(int x=bx1;x<=bx2;x++) {
						for(int y=by1;y<=by2;y++) {
							Region r = regions[x][y];
							r.setRegionId(regionId);
							r.setDescription(description);
							r.setMusicId(musicId);
							
							regionCount++;
						}
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return regionCount;
	}
	
	public Region getRegion(int rx, int ry) {
		return this.regions[rx][ry];
	}
	
	/**
	 * Gets the players in the regions adjacent to the referenced one.
	 * @param rx
	 * @param ry
	 * @return Players in a 7x7 area around the referenced Region.
	 */
	public ArrayList<Player> getPlayersInArea(int rx, int ry) {
		ArrayList<Player> areaPlayers = new ArrayList<Player>();
		Collection<Player> allPlayers = Engine.players.getPlayerList();
		
		for(Player p:allPlayers) {
			Point pPos = p.getCurrentRegionPoint();
			
			if(pPos.x >= rx-1 && pPos.x <= rx+1 &&
				pPos.y >= ry-1 && pPos.y <= ry+1) {	//7x7 area should cover the entire minimap, so people dont just appear out of thin air
				areaPlayers.add(p);
			}
		}

		return areaPlayers;
	}
	
	public ArrayList<NPC> getNPCsInArea(int rx, int ry) {
		ArrayList<NPC> areaNpcs = new ArrayList<NPC>();
		NPC[] allNPCs = Engine.npcs.getNPCList();
		
		for(NPC n:allNPCs) {
			Point nPos = n.getCurrentRegionPoint();
			
			if(nPos.x >= rx-1 && nPos.x <= rx+1 &&
				nPos.y >= ry-1 && nPos.y <= ry+1) {	//7x7 area should cover the entire minimap, so people dont just appear out of thin air
				areaNpcs.add(n);
			}
		}
		
		return areaNpcs;
	}

}
