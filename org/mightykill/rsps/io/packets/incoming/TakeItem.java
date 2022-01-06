package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.intents.CoordinateIntent;
import org.mightykill.rsps.io.client.Client;
import org.mightykill.rsps.items.GroundItem;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.util.Misc;

public class TakeItem extends IncomingPacket {
	
	private Player p;

	public TakeItem(byte[] data, Player origin) {
		super(201, 6, data, origin.getClient());
		this.p = origin;
	}

	public void handlePacket() {
		int y = nextUnsignedShortA();
		int x = nextUnsignedShort();
		int id = nextUnsignedShortBigEndianA();
		
		p.setIntent(new CoordinateIntent(p, new Position(x, y, 0), 0) {

			public boolean handleIntent() {
				p.debug(x+", "+y+"; "+(Misc.getDistance(x, y, p.getPosition().x, p.getPosition().y) <= getDistance()));
				return Misc.getDistance(x, y, p.getPosition().x, p.getPosition().y) <= getDistance();
			}

			public void finishIntent() {
				GroundItem groundItem = Engine.groundItems.getItemAtPosition(p, x, y, id);

				if(groundItem != null) {
					if(p.giveItem(groundItem) > 0) {
						Engine.groundItems.destroyGroundItem(groundItem);
						p.playSound(2582);
						//p.refreshInventory();
					}else {
						p.sendMessage("You do not have enough inventory space to hold this item.");
					}
				}
			}
			
		});
	}

}
