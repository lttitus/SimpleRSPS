package org.mightykill.rsps.io.packets.incoming;

import java.awt.Point;
import java.util.ArrayList;

import org.mightykill.rsps.entities.movement.Movement;
import org.mightykill.rsps.entities.player.Player;

public class Walking extends IncomingPacket {
	
	private int fx, fy, junk, numPaths;
	private int[] pathX, pathY;
	private ArrayList<Point> pathingQueue = new ArrayList<Point>();
	private Player p;

	public Walking(int packetId, int packetSize, byte[] data, Player origin) {
		super(packetId, packetSize, data, origin.getClient());
		if(packetId == 119) packetSize -= 14;
		this.p = origin;
		
			p.idleCount = 0;
			p.faceEntity(null);
			p.setIntent(null);
			p.getCombat().setAttacking(null);
			
			numPaths = (packetSize-5)/2;
			pathX = new int[numPaths];
			pathY = new int[numPaths];
			
			fx = nextUnsignedShortBigEndianA();
			fy = nextUnsignedShortA();
			junk = nextSignedByteC();
			
			for(int i=0;i<numPaths;i++) {
				pathX[i] = nextSignedByte();
				pathY[i] = nextSignedByteS();
			}
	}

	public void handlePacket() {
		if(p.isAlive()) {
			p.closeInterface();
			p.closeChatboxInterface();
			
			p.getMovement().clearStepQueue();
			pathingQueue.add(new Point(fx, fy));
			
			for(int i=0;i<numPaths;i++) {
				pathX[i] += fx;
				pathY[i] += fy;
				
				pathingQueue.add(new Point(pathX[i], pathY[i]));
			}
			
			/* Iterate through each goal tile and calculate the steps required to get there
			 * from the player's current position */
			Point current = p.getPosition().getCoords();
			for(Point goal:pathingQueue) {
				p.getMovement().addStepsToQueue(
						Movement.calculateSteps(current, goal),
						false);
				current = goal;
			}
		}
	}

}
