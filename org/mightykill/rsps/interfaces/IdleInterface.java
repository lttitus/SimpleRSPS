package org.mightykill.rsps.interfaces;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendString;

public class IdleInterface extends Interface {
	
	private long initTime;

	public IdleInterface(Player p, long initTime) {
		super(195, 1, 548, 8);
		
		this.initTime = initTime;
	}

	public void close(Player p) {
		p.sendMessage("You are no longer AFK.");
	}

	public void show(Player p) {
		p.sendPacket(new SendString("", 195, 5));
		p.sendPacket(new SendString("You are AFK", 195, 7));
		p.sendPacket(new SendString("Ticks until disconnect:", 195, 8));
		p.sendPacket(new SendString(""+(initTime+(20*16)), 195, 9));
	}

	public void update(Player p, long curTick) {
		p.sendPacket(new SendString("", 195, 5));
		p.sendPacket(new SendString("You are AFK", 195, 7));
		p.sendPacket(new SendString("Ticks until disconnect:", 195, 8));
		p.sendPacket(new SendString(""+((20*16)+initTime-curTick), 195, 9));
	}

}
