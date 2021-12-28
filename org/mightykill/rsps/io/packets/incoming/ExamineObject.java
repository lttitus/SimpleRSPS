package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;

public class ExamineObject extends IncomingPacket {
	
	private Player p;

	public ExamineObject(byte[] data, Player origin) {
		super(84, 2, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		p.sendMessage("Object examine");
	}

}
