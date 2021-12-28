package org.mightykill.rsps.io.packets.outgoing;

public class SendMessage extends OutgoingPacket {

	public SendMessage(String message) {
		super(218, -1, true);
		addString(message);
	}

}
