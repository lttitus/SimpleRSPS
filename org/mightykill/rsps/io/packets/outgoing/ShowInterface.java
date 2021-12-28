package org.mightykill.rsps.io.packets.outgoing;

public class ShowInterface extends OutgoingPacket {

	/**
	 * Send a packet to show an Interface on the Player's screen
	 * @param interfaceId The Id of the Interface to show
	 * @param location The location where it should be placed on the screen (8 is default)
	 * @param windowId The type of Window that it will be displayed on (548 is default)
	 * @param showId Is the Interface an Overlay, Standalone, etc
	 */
	public ShowInterface(int interfaceId, int location, int windowId, int showId) {
		super(93, 7);
		
		addShort(interfaceId);
		addByteA(showId);
		addShort(windowId);
		addShort(location);
	}
	
	public ShowInterface(int interfaceId, boolean isOverlay) {
		this(interfaceId, 8, 548, isOverlay?1:0);
	}
	
	public ShowInterface(int interfaceId) {
		this(interfaceId, 8, 548, 0);
	}

}
