package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;

public class CameraPosition extends IncomingPacket {
	
	private Player p;

	public CameraPosition(byte[] data, Player origin) {
		super(99, 4, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		//System.out.println("KeyEvent: "+PacketUtils.humanify(data));
		int height = nextUnsignedShort();
		int angle = nextUnsignedShort();
		
		p.idleCount = 0;
		//System.out.println("Camera: "+height+", "+angle);
	}

}
