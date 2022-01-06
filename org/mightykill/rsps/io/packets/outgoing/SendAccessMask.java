package org.mightykill.rsps.io.packets.outgoing;

public class SendAccessMask extends OutgoingPacket {

	/**
	 * 
	 * @param set
	 * @param window
	 * @param interfaceId
	 * @param offset
	 * @param length
	 */
	public SendAccessMask(int length, int offset, int window, int interfaceId, int set, int last) {
		super(223, 12);
		
		addShort(length);
		addShortBigEndianA(offset);
		addShortBigEndian(window);
		addShortBigEndian(interfaceId);
		addShortBigEndian(set);
		addShortBigEndian(last);
	}

}
