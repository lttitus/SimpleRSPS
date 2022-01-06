package org.mightykill.rsps.interfaces;

import org.mightykill.rsps.entities.player.Player;

public abstract class Interface {
	
	protected int interfaceId;
	private int showId;
	private int windowId;
	private int location;
	
	public Interface(int interfaceId, int showId, int windowId, int location) {
		this.interfaceId = interfaceId;
		this.showId = showId;
		this.windowId = windowId;
		this.location = location;
	}

	public int getInterfaceId() {
		return this.interfaceId;
	}
	
	public int getShowId() {
		return this.showId;
	}
	
	public int getWindowId() {
		return this.windowId;
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public abstract void show(Player p);
	
	public abstract void update(Player p);
	
	public abstract void close(Player p);

}
