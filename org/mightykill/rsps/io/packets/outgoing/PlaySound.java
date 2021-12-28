package org.mightykill.rsps.io.packets.outgoing;

public class PlaySound extends OutgoingPacket {

	public PlaySound(int soundId, int j) {
		super(119, 5);
		addShort(soundId);
		addByte(j);
		addShort(0);	//Delay
	}

}
