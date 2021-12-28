package org.mightykill.rsps.world.zones;

import java.awt.Point;
import java.awt.Rectangle;

import org.mightykill.rsps.entities.Entity;

public class Zone {
	
	private int zoneId;
	private int boundx1, boundy1;
	private int boundx2, boundy2;
	private boolean ispvp = false;
	private boolean ismulti = false;
	
	public Zone(int id, int bx1, int by1, int bx2, int by2, boolean ispvp, boolean ismulti) {
		this.zoneId = id;
		this.boundx1 = bx1;
		this.boundx2 = bx2;
		this.boundy1 = by1;
		this.boundy2 = by2;
		this.ispvp = ispvp;
		this.ismulti = ismulti;
	}

	public boolean isPvp() {
		return ispvp;
	}

	public boolean isMulti() {
		return ismulti;
	}

	public int getZoneId() {
		return zoneId;
	}

	public Rectangle getBounds() {
		return new Rectangle(boundx1, boundy1, boundx2, boundy2);
	}
	
	public boolean withinBounds(Entity e) {
		Point pos = e.getPosition().getCoords();
		
		return (pos.x >= boundx1 && pos.x <= boundx2 &&
				pos.y >= boundy1 && pos.y <= boundy2);
	}

}
