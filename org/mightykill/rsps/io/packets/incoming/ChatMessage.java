package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.PacketUtils;
import org.mightykill.rsps.util.Misc;

public class ChatMessage extends IncomingPacket {
	
	private Player p;

	public ChatMessage(byte[] data, Player origin) {
		super(222, -1, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		p.chatMessageEffects = this.nextUnsignedShort();
		int chatLength = this.nextUnsignedByte();
		p.chatMessage = Misc.decryptPlayerChat(this, chatLength);
		p.appearanceUpdated = true;
	}

}
