package org.mightykill.rsps.io.packets.incoming;

import java.io.IOException;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.client.ClientSocket;
import org.mightykill.rsps.util.Misc;

public class UpdateServer extends IncomingPacket {

	public UpdateServer(byte[] data, Client origin) {
		super(15, 4, data, origin);
	}
	
	public void handlePacket() {
		try {
			ClientSocket s = origin.getSocket();
			
			int version = nextInt();
			//System.out.println("Version: "+version);
			
			if(version == 508) {
				s.writeByte((byte)0);
				s.flush();
				
				for (int i = 0; i < Misc.uKeys.length; i++) {	//Cache file data
                    s.writeByte((byte)Misc.uKeys[i]);
                }
				s.flush();
				
				//System.out.println("Sent file cache keys");
				byte b;
				do {
					b = s.readByte();
					//System.out.println(b1);
					//Ignore, this makes the Client happy :)
				}while(b != -1);
				
				//s.close();
			}else {
				s.writeByte((byte)6);	//Out of date client
				s.flush();
			}
		} catch(IndexOutOfBoundsException oobe) {
			oobe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//origin.close();
		//origin.setDone(true);	//Mark this Client for removal
	}

}
