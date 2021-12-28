package org.mightykill.rsps.io.packets.outgoing;

public class SendChatOptionVisibility extends OutgoingPacket {

	public SendChatOptionVisibility(int pub, int priv, int trade) {
		super(186, 3);
		addByte(pub);
		addByte(priv);
		addByte(trade);
	}

}
