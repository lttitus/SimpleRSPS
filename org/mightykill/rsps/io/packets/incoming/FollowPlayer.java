package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.intents.FollowIntent;

public class FollowPlayer extends IncomingPacket {
	
	private Player follower;

	public FollowPlayer(byte[] data, Player origin) {
		super(37, 2, data, origin.getClient());
		this.follower = origin;
	}

	public void handlePacket() {
		int playerId = nextUnsignedShort();
		/*Player followee = Engine.players.getPlayerFromLocalId(playerId);
		
		follower.faceEntity(followee);
		follower.setIntent(new FollowIntent(follower, followee));*/
	}

}
