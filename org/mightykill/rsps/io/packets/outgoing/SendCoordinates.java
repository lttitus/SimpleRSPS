package org.mightykill.rsps.io.packets.outgoing;

import java.awt.Point;

import org.mightykill.rsps.entities.player.Player;

public class SendCoordinates extends OutgoingPacket {

	public SendCoordinates(Player p, int cx, int cy) {
		super(177, 2);
		Point activeChunk = p.getMovement().getActiveChunk();	//The central chunk of the MapRegion
		
		int newx = cx - ((activeChunk.x-6)*8);
		int newy = cy - ((activeChunk.y-6)*8);
		
		addByte(newy);
		addByteS(newx);
		
		p.debug("Sending coords: "+newx+", "+newy);
	}

}
