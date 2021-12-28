package org.mightykill.rsps.io.packets.outgoing;

public class SendScript extends OutgoingPacket {

	public SendScript(int id, Object[] params, String types) {
		super(152, 0, true, true);
		
		addString(types);
		int j=0;
		for(int i=types.length()-1;i >= 0;i--, j++) {
			if(types.charAt(i) == 115) {
				addString((String) params[j]);
			}else {
				addInt((Integer) params[j]);
			}
		}
		addInt(id);
	}

}
