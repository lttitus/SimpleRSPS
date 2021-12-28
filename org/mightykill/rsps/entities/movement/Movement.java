package org.mightykill.rsps.entities.movement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.MapRegion;
import org.mightykill.rsps.world.regions.Region;

public class Movement {
	
	/**
	 * Contains the directions the player will need to step
	 * in order to get to the goal position.
	 */
	private ArrayList<Integer> stepQueue = new ArrayList<Integer>();
	private Entity origin;
	private int currentDirection = 0x06;	//6 = South
	private Position worldPosition;
	private Point activeChunk;
	//private ArrayList<Point> pathQueue = new ArrayList<Point>();
	private boolean running = false;
	private int runEnergy = 10000;
	public boolean teleported = false;
	public int walkDir = -1;
	public int runDir = -1;
	private Region currRegion = null;
	
	public Movement(Entity origin) {
		this.origin = origin;
	}
	
	public void setRun(boolean run) {
		this.running = run;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public Position getPosition() {
		return this.worldPosition;
	}
	
	public void teleport(Position pos) {
		setPosition(pos);
		teleported = true;
		//Entity.appearanceUpdated = true;
	}
	
	public void setActiveChunk(Point chunkPoint) {
		this.activeChunk = chunkPoint;
	}
	
	public Point getActiveChunk() {
		return this.activeChunk;
	}
	
	public void setPosition(Position pos) {	//FIXME
		//Position oldPos = worldPosition;
		this.worldPosition = pos;
		//if(currRegion != null) currRegion.removeEntity(e);
		//Point regionPoint = Region.getRegionPoint(pos.x, pos.y);
		
		//Region newRegion = Engine.regions.getRegion(regionPoint.x, regionPoint.y);
		//newRegion.addEntity(e);
		//currRegion = newRegion;
		
		//return newRegion;
		//Point oldRegion = getMapRegion();
		
		//
		//Engine.regions.getRegion(oldRegion.x, oldRegion.y).removeEntity(e);
		//Engine.regions.getRegion(newRegion.x, newRegion.y).addEntity(e);
	}
	
	/*public Point getMapRegion() {
		Point coords = worldPosition.getCoords();
		int mapX = coords.x >> 3;
		int mapY = coords.y >> 3;
		
		return new Point(mapX, mapY);
	}*/
	
	public void setDirection(int newDirection) {
		this.currentDirection = newDirection;
	}
	
	public int getCurrentDirection() {
		return this.currentDirection;
	}
	
	public void progress(Entity e) {
		Iterator<Integer> stepIterator = stepQueue.iterator();
		if(stepIterator.hasNext()) {
			//e.faceEntity(null);
			int newDir = walkDir = stepIterator.next();
			stepIterator.remove();
			Point newPos = Movement.getCoordsFromDirection(worldPosition.getCoords(), walkDir);
			
			if(running) {	//You move 2 tiles/tick while running; https://runescape.fandom.com/wiki/Energy
				if(stepIterator.hasNext()) {
					newDir = runDir = stepIterator.next();
					stepIterator.remove();
					newPos = Movement.getCoordsFromDirection(newPos, runDir);
				}
			}
			
			setPosition(new Position(newPos.x, newPos.y, worldPosition.h));
			setDirection(newDir);
		}
	}
	
	public static int getDirectionFromCoords(Point oldCoords, Point newCoords) {
		int dx = newCoords.x-oldCoords.x;
		int dy = newCoords.y-oldCoords.y;
		
		//0 +1 2
		//3 -1 4
		//5 +6 7
		
		if (dx < 0) {
            if (dy < 0)
                return 5;
            else if (dy > 0)
                return 0;
            else
                return 3;
        } else if (dx > 0) {
            if (dy < 0)
                return 7;
            else if (dy > 0)
                return 2;
            else
                return 4;
        } else {
            if (dy < 0)
                return 6;
            else if (dy > 0)
                return 1;
            else
                return -1;
        }
	}
	
	public static Point getCoordsFromDirection(Point oldCoords, int direction) {
		switch(direction) {
		case 0:	//BL
			return new Point(oldCoords.x-1, oldCoords.y+1);
		case 1:	//B
			return new Point(oldCoords.x, oldCoords.y+1);
		case 2:	//BR
			return new Point(oldCoords.x+1, oldCoords.y+1);
		case 3:	//L
			return new Point(oldCoords.x-1, oldCoords.y);
		case 4:	//R
			return new Point(oldCoords.x+1, oldCoords.y);
		case 5:	//TL
			return new Point(oldCoords.x-1, oldCoords.y-1);
		case 6:	//T
			return new Point(oldCoords.x, oldCoords.y-1);
		case 7:	//TR
			return new Point(oldCoords.x+1, oldCoords.y-1);
		case -1:	//No change
			return oldCoords;
		}
		
		return oldCoords;
	}
	
	public static ArrayList<Integer> calculateSteps(Point from, Point to) {
		ArrayList<Integer> steps = new ArrayList<Integer>();
		Point current = from;
		
		for(;;) {
			int nextDir = getDirectionFromCoords(current, to);
			current = getCoordsFromDirection(current, nextDir);
			steps.add(nextDir);
			if(current.x == to.x && current.y == to.y) break;
		}
		
		return steps;
	}
	
	public void addStepsToQueue(ArrayList<Integer> steps, boolean force) {
		if(force) clearStepQueue();
		
		this.stepQueue.addAll(steps);
	}
	
	public ArrayList<Integer> getStepQueue() {
		return this.stepQueue;
	}
	
	public void clearStepQueue() {
		this.stepQueue.clear();
	}
	
	public Region getCurrentRegion() {
		Point p = getCurrentRegionPoint();
		return Engine.regions.getRegion(p.x, p.y);
				
		//return this.currRegion;
	}
	
	public Point getCurrentRegionPoint() {
		return new Point(
				worldPosition.x >> 3,
				worldPosition.y >> 3);
	}
	
	/*public void addPointsToQueue(ArrayList<Point> steps) {
		this.pathQueue.addAll(steps);
	}
	
	public ArrayList<Point> getPathingQueue() {
		return this.pathQueue;
	}
	
	public void clearPathing() {
		this.pathQueue.clear();
	}*/

}
