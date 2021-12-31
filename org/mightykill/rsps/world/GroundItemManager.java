package org.mightykill.rsps.world;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.Entity;
import org.mightykill.rsps.entities.movement.Position;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.CreateGroundItem;
import org.mightykill.rsps.io.packets.outgoing.DestroyGroundItem;
import org.mightykill.rsps.io.packets.outgoing.SendCoordinates;
import org.mightykill.rsps.items.GroundItem;
import org.mightykill.rsps.items.Item;

public class GroundItemManager {
	
	private Connection connection;
	//private static ArrayList<GroundItem> CONSTANT_ITEMS = new ArrayList<GroundItem>();
	private static ArrayList<GroundItem> GROUND_ITEMS = new ArrayList<GroundItem>();
	
	public GroundItemManager(Connection sqlConnection) {
		this.connection = sqlConnection;
	}
	
	public void process(long curTick) {
		/*for(GroundItem item:CONSTANT_ITEMS) {
			item.process(curTick);
		}*/
		
		GroundItem[] items = new GroundItem[GROUND_ITEMS.size()];
		items = GROUND_ITEMS.toArray(items);
		for(GroundItem item:items) {
			item.process(curTick);
			
			if(item.getPublicTimer() >= 100 && !item.isPublic()) {	//After one minute, show it to all surrounding players
				sendItemToSurroundingPlayers(item);
				item.makePublic();
			}
			if(item.getPublicTimer() >= 500) {	//5 minutes, delete item from ground
				destroyGroundItem(item);
			}
		}
	}
	
	public boolean destroyGroundItem(GroundItem item) {
		if(item.isPublic()) {
			destroyItemForSurroundingPlayers(item);
		}else {
			if(item.getOwner() instanceof Player) {
				destroyItemForPlayer((Player)item.getOwner(), item);
			}
		}
		
		return GROUND_ITEMS.remove(item);
	}
	
	public void createGroundItem(int itemId, int itemAmount, int x, int y, Entity owner) {
		GroundItem existing = getItemAtPosition(owner, x, y, itemId);
		
		if(existing != null && Engine.items.getDefinition(itemId).isStackable()) {	//There is already an Item here we can add to
			existing.addAmount(itemAmount);
			existing.resetTimer();
		}else {
			GroundItem gi = new GroundItem(itemId, itemAmount, x, y, owner);
			
			GROUND_ITEMS.add(gi);
			if(owner instanceof Player) {
				sendItemToPlayer((Player)owner, gi);
			}
		}
	}
	
	public void createGroundItem(Item item, int x, int y, Entity owner) {
		createGroundItem(item.getItemId(), item.getItemAmount(), x, y, owner);
	}
	
	public boolean takeItem(GroundItem item, Entity taker) {
		return taker.giveItem(item.getItemId(), item.getItemAmount()) && destroyGroundItem(item);
	}
	
	/**
	 * Checks the surrounding area for any items on the ground
	 * @param e 
	 * @return A collection of ground items
	 */
	public ArrayList<GroundItem> checkAreaForItems(Entity e) {
		Position ePos = e.getPosition();
		ArrayList<GroundItem> items = new ArrayList<GroundItem>();
		
		GroundItem[] allItems = new GroundItem[GROUND_ITEMS.size()];
		allItems = GROUND_ITEMS.toArray(allItems);
		for(GroundItem item:allItems) {
			Position p = item.getPosition();
			
			if(p.x >= ePos.x-32 && p.y >= ePos.y-32 &&
				p.y <= ePos.x+32 && p.y <= ePos.y+32 &&
				(item.getOwner() == e || item.getPublicTimer() == 0)) {
				items.add(item);
			}
		}
		
		return items;
	}
	
	public void sendItemToPlayer(Player p, GroundItem item) {
		Position iPos = item.getPosition();

		p.sendPacket(new SendCoordinates(p, iPos.x, iPos.y));
		p.sendPacket(new CreateGroundItem(item.getItemId(), item.getItemAmount()));
	}
	
	public void destroyItemForPlayer(Player p, GroundItem item) {
		Position iPos = item.getPosition();
		
		p.sendPacket(new SendCoordinates(p, iPos.x, iPos.y));
		p.sendPacket(new DestroyGroundItem(item.getItemId()));
	}
	
	/**
	 * Creates a Ground Item for all players in the area EXCEPT the owner, since it's already there
	 * @param item The Item to create
	 */
	public void sendItemToSurroundingPlayers(GroundItem item) {
		/*Collection<Player>*/Player[] allPlayers = Engine.players.getPlayerList();
		//allPlayers.remove(item.getOwner());
		
		for(Player p:allPlayers) {
			if(p != null && item.getOwner() != p) {
				Position iPos = item.getPosition();
				Position pPos = p.getPosition();
				
				if(pPos.x >= iPos.x-32 && pPos.y >= iPos.y-32 &&
					pPos.x <= iPos.x+32 && pPos.y <= iPos.y+32) {
					sendItemToPlayer(p, item);
				}
			}
		}
	}
	
	public void destroyItemForSurroundingPlayers(GroundItem item) {
		Player[] allPlayers = Engine.players.getPlayerList();
		
		for(Player p:allPlayers) {
			if(p != null) {
				Position iPos = item.getPosition();
				Position pPos = p.getPosition();
				
				if(pPos.x >= iPos.x-32 && pPos.y >= iPos.y-32 &&
					pPos.x <= iPos.x+32 && pPos.y <= iPos.y+32) {
					destroyItemForPlayer(p, item);
				}
			}
		}
	}

	/**
	 * Check for an item that this Entity can see at this position
	 * @param e
	 * @param x
	 * @param y
	 * @param id
	 * @return
	 */
	public GroundItem getItemAtPosition(Entity e, int x, int y, int id) {
		GroundItem[] allItems = new GroundItem[GROUND_ITEMS.size()];
		allItems = GROUND_ITEMS.toArray(allItems);
		for(GroundItem item:allItems) {
			Position iPos = item.getPosition();
			if(iPos.x == x && iPos.y == y && item.getItemId() == id && (item.getOwner() == e || item.getOwner() == null)) {
				return item;
			}
		}
		
		return null;
	}
	
	/**
	 * Check for a public item at this position
	 * @param x
	 * @param y
	 * @param id
	 * @return
	 */
	public GroundItem getItemAtPosition(int x, int y, int id) {
		return getItemAtPosition(null, x, y, id);
	}

}
