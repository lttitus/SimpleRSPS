package org.mightykill.rsps.io.packets.outgoing;

public class SendRunEnergy extends OutgoingPacket {

	public SendRunEnergy(int energy) {
		super(99, 1);
		addByte((int)Math.round(energy/2047.0*100));
	}

}
