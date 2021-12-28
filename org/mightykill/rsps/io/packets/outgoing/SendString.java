package org.mightykill.rsps.io.packets.outgoing;

public class SendString extends OutgoingPacket {

	public SendString(String s, int interfaceId, int childId) {
		super(179, s.length()+5, true, true);	//+5 = NUL terminator + 2 shorts, written as a short; Can send 65,020 characters
		addString(s);
		addShort(childId);
		addShort(interfaceId);
	}

}
