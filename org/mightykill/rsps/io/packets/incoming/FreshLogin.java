package org.mightykill.rsps.io.packets.incoming;

import java.io.IOException;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.LoginResult;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.client.ClientSocket;
import org.mightykill.rsps.io.packets.outgoing.FriendServer;
import org.mightykill.rsps.io.packets.outgoing.MapRegion;
import org.mightykill.rsps.io.packets.outgoing.PlayerOption;
import org.mightykill.rsps.io.packets.outgoing.SendConfig;
import org.mightykill.rsps.io.packets.outgoing.SendSkill;
import org.mightykill.rsps.io.packets.outgoing.WindowSize;
import org.mightykill.rsps.util.Misc;

public class FreshLogin extends IncomingPacket {

	public FreshLogin(int packetId, int packetSize, byte[] data, Client origin) {
		super(packetId, packetSize, data, origin);
	}

	public void handlePacket() {
		//System.out.println("Packet size: "+packetSize);
		int returnCode = -1;
		int version = nextInt();
		//System.out.println("Version: "+version);
		//Player p = null;
		
		if(version == 508) {
			int[] zs = nextUnsignedBytes(3);	//Zeros
			
			int cWidth = nextUnsignedShort();
			int cHeight = nextUnsignedShort();
			origin.setDimensions(cWidth, cHeight);
			
			String params = nextString();
			//System.out.println("Parameters: "+params);
			
			int affilId = nextInt();
			int using22 = nextInt();
			
			int[] cacheKeys = new int[31];
			for(int i=0;i<31;i++) {
				cacheKeys[i] = nextInt();
			}
			int parity = nextUnsignedShort();
			
			long userLong = nextLong();
			String username = Misc.longToString(userLong).toLowerCase().replace('_', ' ').trim();
			String password = nextString();
			
			LoginResult res = Engine.players.login(origin, username, password);
			//p = //Engine.players.createPlayer(origin, username, password);
			//p = Engine.createPlayer(origin, username);
			
			try {
				ClientSocket sock = origin.getSocket();
				
				if(res != null) returnCode = res.getOpcode(); else returnCode = 0;
				sock.writeByte((byte)returnCode);
				sock.flush();
				
				if(returnCode == LoginResult.SUCCESS) {
					Player p = res.getPlayer();
					
					if(p != null) {
						sock.writeByte((byte)p.getRights());
						sock.writeByte((byte)0);
						sock.writeByte((byte)0);
						sock.writeByte((byte)0);
						sock.writeByte((byte)1);
						sock.writeByte((byte)0);
						sock.writeByte((byte)p.getWorldIndex());
						sock.writeByte((byte)1);	//Members?
						sock.flush();
						
						origin.sendPacket(new MapRegion(p));
						
						origin.sendPacket(new WindowSize(false));
						
						p.initInterfaces();
						
						for(int i=0;i<24;i++ ) {
							origin.sendPacket(new SendSkill(p, i));
						}
						
						p.refreshInventory();
						p.refreshEquipment();
						
						//p.sendPacket(new PlayerOption("Attack", 1));
						p.sendPacket(new PlayerOption("Follow", 2));
						p.sendPacket(new PlayerOption("Trade", 3));
						
						p.sendPacket(new SendConfig(173, 0));
						p.sendPacket(new SendConfig(313, -1));
						p.sendPacket(new SendConfig(465, -1));
						p.sendPacket(new SendConfig(802, -1));
						p.sendPacket(new SendConfig(1085, 249852));
						
						p.sendPacket(new FriendServer());
						
						
						
						p.loggedIn();
					}else {
						sock.flush();
						System.err.println("Something went wrong!");
						//Engine.removePlayer(p.getId());
					}
				}else {
					System.err.println(username+" was unable to login!");
					sock.writeByte((byte)1);
					sock.flush();
				}
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}else {
			System.err.println("Bad client version");
			returnCode = 5;
		}
		
		
	}

}
