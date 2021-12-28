package org.mightykill.rsps.io.packets.incoming;

import java.awt.Point;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.npc.NPC;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.exchange.GrandExchange;

public class ObjectInteraction extends IncomingPacket {
	
	private Player p;

	public ObjectInteraction(int packetId, int packetSize, byte[] data, Player origin) {
		super(packetId, packetSize, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int objx = -1, objy = -1, objid = -1, interact = -1;
		//TODO: Verify object locations
		
		switch(packetId) {
		case 158:	//First option
			objx = nextUnsignedShortBigEndian();
			objid = nextUnsignedShort();
			objy = nextUnsignedShortBigEndianA();
			interact = 1;
			
			switch(objid) {
			case 24389:	//Knock on door thing
				
				break;
			}
			
			break;
		case 228:	//Second option
			objy = nextUnsignedShortA();
			objid = nextUnsignedShortBigEndian();
			objx = nextUnsignedShortBigEndianA();
			interact = 2;
			
			if(objid == 28089) {	//GE Booth
				GrandExchange.openOfferScreen(p);
			}
			
			break;
		case 46:	//Third option
			objy = nextUnsignedShortBigEndianA();
			objx = nextUnsignedShortBigEndianA();
			objid = nextUnsignedShortA();
			interact = 3;
			
			if(objid == 28089 || objid == 11402) {	//Bank Booths
				GrandExchange.openCollection(p);
			}
			
			break;
			
		case 94:	//Fourth option
			objx = nextUnsignedShortA();
			objid = nextUnsignedShortBigEndian();
			objy = nextUnsignedShortBigEndianA();
			interact = 4;
			
			if(objid == 28089) {	//GE Booth
				GrandExchange.openHistory(p);
			}
			
			break;
			
		case 190:	//Fifth option
			objy = nextUnsignedShortBigEndian();
			objx = nextUnsignedShortBigEndianA();
			objid = nextUnsignedShortBigEndianA();
			interact = 5;
			
			if(objid == 28089) {	//GE Booth
				GrandExchange.openItemSetsScreen(p);
			}
			
			break;
		}
		
		p.faceCoords(new Point(objx, objy));
		p.sendMessage("Object("+interact+"): "+objid+"; "+objx+", "+objy);
		//System.out.println("Data: "+PacketUtils.humanify(this.data));
	}

}
