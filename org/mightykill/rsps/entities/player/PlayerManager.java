package org.mightykill.rsps.entities.player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.util.Misc;

public class PlayerManager {
	
	private MessageDigest md;
	private Connection sql;

	private Player[] playerList = new Player[2048];
	
	public PlayerManager(Connection sqlConnection) {
		try {
			md = MessageDigest.getInstance("SHA-256");
			sql = sqlConnection;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private int getNextFreeSlot() {
		for(int slot=1;slot<playerList.length;slot++) {
			if(playerList[slot] == null) return slot;
		}
		
		return -1;
	}
	
	public ArrayList<Player> getPlayersInArea(Entity e) {
		ArrayList<Player> players = new ArrayList<Player>();
		int numPlayers = 0;
		
		for(Player p:playerList) {
			if(numPlayers == 255) break; 
			
			if(p != null) {
				if(Misc.withinDistance(e, p)) {
					players.add(p);
					numPlayers++;
				}
			}
		}
		
		return players;
	}
	
	public Player findPlayerByUUID(String uuid) {
		for(int slot=0;slot<playerList.length;slot++) {
			Player p = playerList[slot];
			if(p != null) {
				if(p.getUUID().equalsIgnoreCase(uuid)) {
					return playerList[slot];
				}
			}
		}
		
		return null;
	}
	
	public Player getPlayer(int worldId) {
		return this.playerList[worldId];
	}
	
	public LoginResult login(Client c, String username, String password) {
		try {
			PreparedStatement ps = sql.prepareStatement("SELECT * FROM players WHERE username=?");
			ps.setString(1, username.toLowerCase());
			boolean exe = ps.execute();
			
			if(exe) {
				ResultSet result = ps.getResultSet();
				if(result.next()) {
					String uuid = result.getString("uuid");
					
					if(uuid != null) {
						Player existingPlayer = findPlayerByUUID(uuid);
						
						if(existingPlayer == null) {
							String passhash = Misc.getHexDump(md.digest(password.getBytes()));
							
							if(passhash.equals(result.getString("passhash"))) {
								int id = getNextFreeSlot();
								int rights = result.getInt("rights");
								String rawXP = result.getString("stat_xp");
								int absx = result.getInt("absx");
								int absy = result.getInt("absy");
								
								if(id != -1) {
									String connectingIp = c.getSocket().getAddress().getCanonicalHostName();
									long connectTime = System.currentTimeMillis();
									PreparedStatement updateconnect = sql.prepareStatement("UPDATE players SET lastconnectip=?, lastconnecttime=? WHERE uuid=?");
									updateconnect.setString(1, connectingIp);
									updateconnect.setLong(2, connectTime);
									updateconnect.setString(3, uuid);
									
									try {
										updateconnect.execute();
									} catch(SQLException e) {
										System.err.println("Unable to update lastconnect for "+username+".");
									}
									
									
									Player p = new Player(c, id, username, uuid, rights, rawXP, new Position(absx, absy, 0));
									System.out.println("Player "+username+" logged in successfully!");
									
									playerList[id] = p;
									
									return new LoginResult(LoginResult.SUCCESS, p);
								}
							}else {
								return new LoginResult(LoginResult.INVALID_INFORMATION, null);
							}
						}else {	//Already logged in
							return new LoginResult(LoginResult.ALREADY_LOGGED_IN, existingPlayer);	//TODO: Pass this back and maybe use it for re-logging (packet 18, etc)
						}
					}
				}else {
					System.err.println("Player does not exist; Creating one now...");
					if(createPlayer(c, username, password)) {
						return login(c, username, password);
					}else {
						System.err.println("Unable to create Player account!");
						return new LoginResult(11, null);
					}
				}
			}else {
				System.err.println("Something happened...");
				return new LoginResult(11, null);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return new LoginResult(11, null);
		}
		
		return null;
	}
	
	public int removePlayer(Player p) {
		for(int slot=0;slot<playerList.length;slot++) {
			if(playerList[slot] == p) {
				playerList[slot] = null;
				return slot;
			}
		}
		
		return -1;
	}
	
	private String generateUUID(Object... data) {
		StringBuilder sb = new StringBuilder();
		
		for(Object d:data) {
			sb.append(d);
		}
		
		String raw = sb.toString();
		return Misc.getHexDump(md.digest(raw.getBytes()));
	}
	
	public boolean createPlayer(Client c, String username, String password) {
		String passhash = Misc.getHexDump(md.digest(password.getBytes()));
		String connectip = c.getSocket().getAddress().getCanonicalHostName();
		
		try {
			PreparedStatement create = sql.prepareStatement("INSERT INTO players (username, passhash, lastconnectip, lastconnecttime) VALUES (?, ?, ?, ?)");
			create.setString(1, username);
			create.setString(2, passhash);
			create.setString(3, connectip);
			create.setLong(4, System.currentTimeMillis());
			
			create.execute();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String getSkillString(int[] skillLevels, int[] skillXp) {
		String xp = "";
		for(int skillId=0;skillId<24;skillId++) {
			xp += String.format("%02X", skillLevels[skillId]);
			xp += String.format("%07X", skillXp[skillId]);
		}
		
		return xp;
	}
	
	public boolean savePlayer(Player p) {
		Position pPos = p.getPosition();
		int absx = pPos.x;	//TODO: Map location verification so we don't get stuck outside the map
		int absy = pPos.y;
		String uuid = p.getUUID();
		String skills = getSkillString(p.getAllLevels(), p.getAllXP());
		
		if(skills.length() == 216) {	//Length in 4-bit chars this string will need to be to be complete
			try {
				
				PreparedStatement updateuser = sql.prepareStatement("UPDATE players SET stat_xp=?, absx=?, absy=? WHERE uuid=?");
				updateuser.setString(1, skills);
				updateuser.setInt(2, absx);
				updateuser.setInt(3, absy);
				updateuser.setString(4, uuid);
			
				if(!updateuser.execute()) {
					System.out.println("Player "+p.getName()+" saved successfully");
					return true;
				}else {
					System.out.println(updateuser.getResultSet().toString());
					//TODO: Error handling
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public Player[] getPlayerList() {
		return this.playerList;
	}

}
