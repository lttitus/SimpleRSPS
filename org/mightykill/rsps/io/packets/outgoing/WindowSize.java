package org.mightykill.rsps.io.packets.outgoing;

public class WindowSize extends OutgoingPacket {

	public WindowSize(boolean fullscreen) {
		super(239, 3);
		addShort(fullscreen ? 752:548);	//752 for fullscreen
		addByteA(0);
	}

}
