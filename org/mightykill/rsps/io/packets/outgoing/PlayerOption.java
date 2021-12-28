package org.mightykill.rsps.io.packets.outgoing;

public class PlayerOption extends OutgoingPacket {

	public PlayerOption(String option, int slot) {
		super(252, option.length()+3, true, false);	//0 + Length of String + NUL + slot
		addByteC(0);	//Not sure why this is needed; self maybe?
		addString(option);
		addByteC(slot);
	}

}
