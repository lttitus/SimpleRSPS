package org.mightykill.rsps.io.packets.outgoing;

public class PlayMusic extends OutgoingPacket {
	
	public PlayMusic(int musicId) {
		super(146, 2);
		addShortBigEndianA(musicId);
	}

}
