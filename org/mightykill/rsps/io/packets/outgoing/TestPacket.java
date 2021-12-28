package org.mightykill.rsps.io.packets.outgoing;

public class TestPacket extends OutgoingPacket {

	public TestPacket(int a, int b) {
		super(177, 1);
		addByte(a);
		addByteS(b);
	}

}
