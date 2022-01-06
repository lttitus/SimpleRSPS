package org.mightykill.rsps.io.packets;

public class PacketUtils {
	
	/**
	 * Static packet size definitions - Inbound and Outbound<br>
	 * -1 = Variable packet size<br>
	 * 0 = Unused (ignored)<br>
	 * # = Number of bytes AFTER packetId
	 */
	public static final int[] PACKET_SIZES = {
		0, 0, 0, 8, 0, 0, 0, 2,		//7
		0, 0, 0, 0, 0, 0, 1, 4,		//15
		-1, -1, -1, 0, 0, 6, 5, 0,	//23
		0, 0, 0, 0, 0, 0, 0, 0,		//31
		0, 0, 0, 0, 0, 2, 2, 0,		//39
		8, 0, 8, 0, 0, 0, 6, 0,		//47
		0, -1, 0, 0, 2, 0, 0, 0,	//55
		0, 0, 0, 6, 0, 0, 0, 0,		//63
		0, 0, 0, 0, 0, 0, 8, 0,		//71
		0, 0, 0, 0, 0, 0, 0, 0,		//79
		0, 0, 0, 0, 2, 6, 0, 0,		//87
		2, 0, 2, 0, 0, 0, 6, 0,		//95
		0, 0, 0, 4, 0, 0, 0, 0,		//103
		0, 0, 0, -1, 0, 0, 0, 0,	//111
		0, 4, 0, 0, 0, -1, 0, -1,	//119
		0, 0, 0, 2, 0, 0, 0, 0,		//127
		0, 0, 0, -1, 0, 0, 0, 0,	//135
		0, 0, -1, 0, 0, 0, 0, 0,	//143
		0, 0, 0, 0, 0, 0, 0, 0,		//151
		0, 0, 0, 0, 0, 0, 6, 0,		//159
		2, 0, 0, 0, 0, 4, 0, 9,		//167
		0, 6, 0, 0, 0, 0, 0, 0,		//175
		0, 0, 0, 12, 0, 0, 0, 17,	//183
		0, 0, 8, 0, 0, 0, 6, 0,		//191
		0, 0, 0, 2, 0, 0, 0, 0,		//199
		0, 6, 0, 8, 0, 0, 0, 0,		//207
		0, 0, 0, 8, 0, 0, 6, 0,		//215
		-1, 0, 0, 0, 8, 0, -1, 0,	//223
		0, 0, 0, 2, 6, 0, 0, 0,		//231
		6, 6, 0, 0, 0, 0, 0, 0,		//239
		0, 0, 0, 0, 0, 0, 0, 4,		//247
		1, 0, 0, 0, 0, 1, 0, 4,		//255
	};
	
	public static String humanify(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		
		int index=0;
		for(byte b:bytes) {
			sb.append(String.format("%02x ", b));
			if(index % 8 == 7) sb.append('\n');
			index++;
		}
		
		return sb.toString().trim();
	}

}
