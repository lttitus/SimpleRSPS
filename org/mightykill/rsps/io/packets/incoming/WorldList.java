package org.mightykill.rsps.io.packets.incoming;

import java.io.IOException;

import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.io.client.ClientSocket;
import org.mightykill.rsps.io.packets.outgoing.RawPacket;

public class WorldList extends IncomingPacket {

	public WorldList(byte[] data, Client origin) {
		super(255, 4, data, origin);
	}

	public void handlePacket() {
		
		ClientSocket s = origin.getSocket();
		RawPacket response = new RawPacket(0, 21, true, false, false);
		
		//TODO: Fix this
		//byte[] raw = s.readBytes(4);
		int request = nextInt();//raw[3] << 24 | raw[2] << 16 | raw[1] << 8 | raw[0];
		//System.out.println(request);
		response.addByte(1);
		response.addShort(1);
		response.addShortBigEndian(0x80 + 3);	//Member's world + player count
		response.addString("Big pp world");
		response.addShort(65536);	//Test?
		response.addShort(77);	//Flag I think
		
		
		/*if(request != 0) {	//Everything
			
			//response.addShort(257);	//0000 0001 0000 0001
			
			//response.addByte(1);	//Num locations
			
			//response.addByte(77);		//Flag Id
			//response.addString("BRUH");	//Name of location
			
			//response.addByte(1);	//Starting world id
			//response.addByte(2);	//Total number+1
			//response.addByte(1);	//Total being sent
			
			//response.addByte(1);	//World id
			//response.addByte(77);	//Location Id
			//response.addInt(1);	//Flags
			//response.addString("-");	//Activity
			//response.addString("127.0.0.1");	//IP
			//response.addInt(0x94DA4A87);	//EOL
		}else {	//Just player counts
			response.addByte(1);	//Player count data?
			response.addByte(0);	//World data?
			
			response.addByte(1);	//World Id
			response.addShort(256);	//Player count
		}*/
	
		origin.sendPacket(response);
		
		//s.close();
	}

}
