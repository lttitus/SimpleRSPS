package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.PacketUtils;

public class MusicPacket extends IncomingPacket {
	
	private Player p;

	public MusicPacket(byte[] data, Player origin) {
		super(247, 4, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		System.out.println(PacketUtils.humanify(data));
		p.isPlayingMusic = !p.isPlayingMusic;
	}

}
