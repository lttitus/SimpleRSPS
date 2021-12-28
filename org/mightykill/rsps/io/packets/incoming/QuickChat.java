package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.io.client.Client;

public class QuickChat extends IncomingPacket {

	public QuickChat(byte[] data, Client origin) {
		super(250, 0, data, origin);
	}

	public void handlePacket() {
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
