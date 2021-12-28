package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.packets.PacketUtils;

public class ReconnectSession extends IncomingPacket {

	public ReconnectSession(int packetId, int packetSize, byte[] data, Client origin) {
		super(packetId, packetSize, data, origin);
	}

	public void handlePacket() {
		System.out.println("Reconnect Session: "+PacketUtils.humanify(data));
		/*System.out.println("More data?");
		int d=-1;
		int i=0;
		try {
			while((d=origin.getSocket().readByte()&0xff)!=-1) {
				i++;
				System.out.println("["+i+"] Got: "+d);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
