package org.mightykill.rsps.entities.movement;

import java.awt.Point;

public class Position {
	
	public int x, y, h;
	
	public Position(int x, int y, int h) {
		this.x = x;
		this.y = y;
		this.h = h;
	}
	
	public Point getCoords() {
		return new Point(x, y);
	}
	
	public int getHeight() {
		return this.h;
	}
	
	public Point getCurrentChunk() {
		return new Point(x >> 3, y >> 3);	//x/8, y/8
	}
	
	public Point getLocalPosition() {
		Point c = getCurrentChunk();
		//System.out.println(mapRegion.x+",. "+mapRegion.y);
		return new Point(
				x-8*(c.x-6),
				y-8*(c.y-6));
	}

}
