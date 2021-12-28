package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;

public class MouseEvent extends IncomingPacket {

	public MouseEvent(byte[] data, Client origin) {
		super(59, 6, data, origin);
	}

	public void handlePacket() {
		int d = nextUnsignedShort();
		int mb = (d & 0x8000) >> 15;	//Should be 0 or 1
		int time = d & 0x7FFF;
		int mx = nextUnsignedShort();
		int my = nextUnsignedShort();
		
		//System.out.println("Button: "+mb+", Last click: "+time+", x: "+mx+", y: "+my);
	}

}
