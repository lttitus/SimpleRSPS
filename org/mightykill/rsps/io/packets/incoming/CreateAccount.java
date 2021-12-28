package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.PacketUtils;

public class CreateAccount extends IncomingPacket {

	public CreateAccount(byte[] data, Client origin) {
		super(85, 6, data, origin);
	}

	public void handlePacket() {
		System.out.println("Create Account:\n"+PacketUtils.humanify(data));
		int day = nextUnsignedByte();
		int month = nextUnsignedByte();
		int junk = nextUnsignedByte();	//7?
		int year = nextUnsignedByte();
		int junk2 = nextUnsignedByte();	//0?
		int location = nextUnsignedByte();
	}

}
