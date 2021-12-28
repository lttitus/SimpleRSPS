package org.mightykill.rsps.io.packets.outgoing;

public class FriendServer extends OutgoingPacket {

	public FriendServer() {
		super(115, 1);
		addByte((byte)2);	//Not sure what this represents. World?
	}

}
