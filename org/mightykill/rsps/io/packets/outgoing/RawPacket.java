package org.mightykill.rsps.io.packets.outgoing;

public class RawPacket extends OutgoingPacket {

	public RawPacket() {
		super(0, 1);
	}
	
	public RawPacket(int packetId, int packetSize) {
		super(packetId, packetSize);
	}
	
	public RawPacket(int packetId, int packetSize, boolean sendSize, boolean isShort) {
		super(packetId, packetSize, sendSize, isShort);
	}
	
	public RawPacket(int packetId, int packetSize, boolean sendSize, boolean isShort, boolean sendId) {
		super(packetId, packetSize, sendSize, isShort, sendId);
	}

}
