package org.mightykill.rsps.io.packets.incoming;

import java.io.IOException;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.client.ClientSocket;

public class InitiateLogin extends IncomingPacket {

	public InitiateLogin(byte[] data, Client origin) {
		super(14, 1, data, origin);
	}

	public void handlePacket() {
		//origin.setProcessing(true);
		
		try {
			ClientSocket s = origin.getSocket();
			
			int derivedFromUser = nextUnsignedByte();
			//System.out.println("Derived from user: "+derivedFromUser);
			
			s.writeByte((byte)0);
			s.writeBytes(new byte[] {0, 0, 0, 0, 0, 0, 0, 0});	//Ignored
			s.flush();
		} catch(IndexOutOfBoundsException oobe) {
			oobe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//origin.setProcessing(false);
	}

}
