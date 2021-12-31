package org.mightykill.rsps.io.packets.outgoing;

import java.awt.Point;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.world.regions.Region;

public class MapRegion extends OutgoingPacket {

	public MapRegion(Player p) {
		super(142, 0, true, true);	//Variable size packet, send size as a short
		
		Point chunk = p.getPosition().getCurrentChunk();
		Point localPos = p.getMovement().getPosition().getLocalPosition();
		p.debug("Active Chunk: "+chunk.x+", "+chunk.y+"; Position: "+localPos.x+", "+localPos.y+", "+0);
		
		addShortA(chunk.x);
		addShortBigEndianA(localPos.y);
		addShortA(localPos.x);
		boolean forceSend = true;
		
		if ((((chunk.x / 8) == 48) || ((chunk.x / 8) == 49)) &&
				((chunk.y / 8) == 48)) {
					forceSend = false;
			}
		if (((chunk.x / 8) == 48) && ((chunk.y / 8) == 148)) {
			forceSend = false;
		}
		for (int xCalc = (chunk.x - 6) / 8; xCalc <= ((chunk.x + 6) / 8); xCalc++) {
		for (int yCalc = (chunk.y - 6) / 8; yCalc <= ((chunk.y + 6) / 8); yCalc++) {
			int region = yCalc + (xCalc << 8);
		
			if (forceSend ||
				((yCalc != 49) && (yCalc != 149) &&
				(yCalc != 147) && (xCalc != 50) &&
				((xCalc != 49) || (yCalc != 47)))) {
					int[] mapData = Engine.mapData.getData(region);
					
					if (mapData == null) {
						p.getMovement().teleport(Engine.HOME);
						p.sendMessage("You got teleported home due to missing MapData.");
						return;
					}
					
					addInt(mapData[0]);
					addInt(mapData[1]);
					addInt(mapData[2]);
					addInt(mapData[3]);
				}
			}
		}
		
		addByteC(0);	//Height
		addShort(chunk.y);
	}

}
