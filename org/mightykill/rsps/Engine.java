package org.mightykill.rsps;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Random;

import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.npc.NPCManager;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.entities.player.PlayerManager;
import org.mightykill.rsps.exchange.GrandExchange;
import org.mightykill.rsps.io.ConnectionManager;
import org.mightykill.rsps.io.packets.incoming.IncomingPacket;
import org.mightykill.rsps.io.packets.outgoing.ClientSyncNPCs;
import org.mightykill.rsps.io.packets.outgoing.ClientSyncPlayers;
import org.mightykill.rsps.io.packets.outgoing.OutgoingPacket;
import org.mightykill.rsps.items.ItemManager;
import org.mightykill.rsps.world.GroundItemManager;
import org.mightykill.rsps.world.regions.RegionManager;
import org.mightykill.rsps.world.zones.ZoneManager;

import palidino76.rs2.world.mapdata.MapData;

public class Engine implements Runnable {
	
	private boolean running = false;
	private static int worldId = -1;
	
	private Connection sqlConnection;
	private ConnectionManager connections;
	private static long tickCount = 0L;
	
	public static Random random = new Random();
	public static PlayerManager players;
	public static MapData mapData = new MapData();
	public static RegionManager regions;
	public static GrandExchange ge;
	public static ItemManager items;
	public static NPCManager npcs;
	public static ZoneManager zones;
	public static GroundItemManager groundItems;
	
	public static final Position HOME = new Position(3190, 3421, 0);
	
	public Engine(int port, int worldId) {
		try {
			Engine.worldId = worldId;
			System.out.println("Started SimpleRSPS on port "+port);
			if(dbConnect()) {
				this.connections = new ConnectionManager(port);
				regions = new RegionManager(sqlConnection, 1024, 1024);
				zones = new ZoneManager(sqlConnection);
				items = new ItemManager(sqlConnection);
				players = new PlayerManager(sqlConnection);
				npcs = new NPCManager(sqlConnection);
				ge = new GrandExchange(sqlConnection);
				groundItems = new GroundItemManager(sqlConnection);
				(new Thread(connections)).start();
				
				this.running = true;
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private boolean dbConnect() {
		try {
			sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/simplersps", "rsps", "simplersps");
		
			System.out.println("Connected to SQL Database.");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void run() {
		long lastUpdate = 0;
		long curTime = System.currentTimeMillis();
		
		while(running) {
			
			/* Receive 0-8 incoming packets from all Players at least every 50ms
			 * This way packets are handled ASAP, rather than waiting for the next game tick. */
			for(Player p:players.getPlayerList()) {
				if(p != null) {
					p.getClient().receivePackets(p);
					
					for(Iterator<IncomingPacket> it=p.getClient().getIncomingPackets().iterator();it.hasNext();) {
						IncomingPacket packet = it.next();
						if(packet != null) {
							packet.handlePacket();
						}
						
						it.remove();
					}
				}
			}
			
			if(curTime-lastUpdate >= 600) {
				
				Player[] playerList = players.getPlayerList();
				NPC[] npcList = npcs.getNPCList();
				
				/* Process all extra content first */
				ge.process(tickCount);
				groundItems.process(tickCount);
				
				/* Update all Players first */
				for(Player p:playerList) {
					if(p != null && p.online) {
						p.processEntity(tickCount);
					}
				}
				
				/* Then process NPC updates */
				for(NPC n:npcList) {
					if(n != null) {
						n.processEntity(tickCount);
					}
				}
				
				/* Queue Client Sync packets to be sent */
				for(Player p:playerList) {
					if(p != null && p.online && p.isConnected()) {
						p.sendPacket(new ClientSyncPlayers(p));
						p.sendPacket(new ClientSyncNPCs(p));
					}
				}
				
				/* Send all Packets to every connected Player's Clients - Update, then clear flags */
				for(Player p:playerList) {
					if(p != null && p.online) {
						ArrayList<OutgoingPacket> packets = p.getClient().getOutgoingPackets();
						try {
							for(Iterator<OutgoingPacket> it=packets.iterator();it.hasNext();) {
								OutgoingPacket packet = it.next();
								
								try {
									packet.sendPacket(p.getClient().getSocket());
								} catch (IOException e) {
									System.err.println("Error sending packet "+packet.getPacketId()+" to "+p.getClient()+"!");
									p.disconnect();
									continue;
								}
								
								it.remove();
							}
						} catch(ConcurrentModificationException cme) {
							p.disconnect();
							players.removePlayer(p);
							continue;
						}
						
						if(!p.isConnected()) {
							p.getClient().getSocket().close();
							players.removePlayer(p);
						}
						
						p.resetFlags();
					}
				}
				
				for(NPC n:npcList) {
					if(n != null) {
						n.resetFlags();
					}
				}

				tickCount++;
				lastUpdate = System.currentTimeMillis();
			}
			curTime = System.currentTimeMillis();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getWorldId() {
		return worldId;
	}
	
	public static NPC getNPC(int Id) {
		return npcs.getNPC(Id);
	}
	
	public int getPort() {
		return connections.getPort();
	}
	
	public static long getTick() {
		return tickCount;
	}

}
