package org.mightykill.rsps.io.packets.outgoing;

import org.mightykill.rsps.entities.player.Player;

public class SendSkill extends OutgoingPacket {

	public SendSkill(Player p, int skillId) {
		super(217, 6);
		addByteC(p.getSkillLevel(skillId));
		addInt_v2(p.getSkillXp(skillId));
		addByteC(skillId);
	}

}
