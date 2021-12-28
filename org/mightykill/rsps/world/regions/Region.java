package org.mightykill.rsps.world.regions;

import java.awt.Point;
import java.util.ArrayList;

import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;

public class Region {
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<NPC> npcs = new ArrayList<NPC>();
	private int regionId = -1;
	private int rx, ry;
	private String description;
	private int musicId;
	
	public Region(int rx, int ry, String description) {
		this.rx = rx;
		this.ry = ry;
		this.description = description;
		this.musicId = -1;
	}
	
	public boolean addEntity(Entity e) {
		if(e instanceof Player) {
			Player p = (Player)e;
			
			/*if(this.musicId != -1 && 
					p.isConnected() && 
					((p.isPlayingMusic && p.getMusicPlaying() != this.musicId) ||
						!p.isPlayingMusic)) {
				p.playMusic(musicId);
			}*/
			
			return this.players.add(p);
		}else {
			return this.npcs.add((NPC)e);
		}
	}
	
	public int getId() {
		return this.regionId;
	}
	
	public boolean removeEntity(Entity e) {
		if(e instanceof Player) {
			return this.players.remove((Player)e);
		}else {
			return this.npcs.remove((NPC)e);
		}
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	
	public ArrayList<NPC> getNPCs() {
		return this.npcs;
	}
	
	public void setRegionId(int id) {
		this.regionId = id;
	}
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public void setMusicId(int musicId) {
		this.musicId = musicId;
	}
	
	public int getMusicId() {
		return this.musicId;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public Point getRegionPoint() {
		return new Point(this.rx, this.ry);
	}
	
	public static Point getRegionPoint(int wx, int wy) {
		return new Point(wx >> 3, wy >> 3);
	}
	
	public int getX() {
		return rx;
	}
	
	public int getY() {
		return ry;
	}

}
