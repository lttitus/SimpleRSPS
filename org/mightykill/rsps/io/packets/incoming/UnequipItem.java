package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.combat.Combat;
import org.mightykill.rsps.entities.player.Appearance;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendString;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.containers.Equipment;
import org.mightykill.rsps.items.containers.Inventory;

public class UnequipItem extends IncomingPacket {
	
	private Player p;

	public UnequipItem(byte[] data, Player origin) {
		super(203, 8, data, origin.getClient());
		
		this.p = origin;
	}

	public void handlePacket() {
		int slot = nextUnsignedShortBigEndianA();
		int interfaceId = nextUnsignedShort();
		int junk = nextUnsignedShort();
		int itemId = nextUnsignedShort();
		
		Equipment equipment = p.getEquipment();
		Inventory inventory = p.getInventory();
		Item unequipItem = equipment.getItemInSlot(slot);
		
		if(unequipItem != null) {
			p.idleCount = 0;
			if(!inventory.addItem(
					itemId, 
					unequipItem.getItemAmount())) {
				p.sendMessage("You do not have enough space in your inventory to remove this item.");
			}else {
				p.getEquipment().setItem(slot, null);
				if(slot == Equipment.WEAPON) {
					p.getCombat().setAttackAnim(Combat.DEFAULT_ATTACK);
					p.setIdleAnimation(Appearance.DEFAULT_IDLE);
					p.setWalkAnimation(Appearance.DEFAULT_WALK);
					p.setRunAnimation(Appearance.DEFAULT_RUN);
					p.setTabInterface(73, 82);
					p.sendPacket(new SendString("Unarmed", 82, 0));
				}
				p.playSound(Engine.items.getEquipment(itemId).getUnequipSound());
				p.refreshEquipment();
				p.refreshInventory();
				p.appearanceUpdated = true;
			}
		}
	}

}
