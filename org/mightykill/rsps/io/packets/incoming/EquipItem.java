package org.mightykill.rsps.io.packets.incoming;

import org.mightykill.rsps.Engine;
import org.mightykill.rsps.entities.player.Player;
import org.mightykill.rsps.io.packets.outgoing.SendString;
import org.mightykill.rsps.items.Item;
import org.mightykill.rsps.items.WeaponDefinition;
import org.mightykill.rsps.items.containers.Equipment;
import org.mightykill.rsps.items.containers.Inventory;

public class EquipItem extends IncomingPacket {
	
	private Player p;

	public EquipItem(byte[] data, Player origin) {
		super(3, 8, data, origin.getClient());

		this.p = origin;
	}

	public void handlePacket() {
		int junk1 = nextInt_v2();
		int itemId = nextUnsignedShortBigEndian();
		int slot = nextUnsignedByte();
		int junk2 = nextUnsignedByte();
		
		Equipment equipment = p.getEquipment();
		Inventory inventory = p.getInventory();
		Item equipItem = inventory.getItemInSlot(slot);
		
		if(equipItem != null) {
			boolean is2h = Engine.items.getHandhelds(itemId).isTwoHand();
			int targetSlot = Engine.items.getEquipment(itemId).getEquipSlot();
			
			p.idleCount = 0;
			if(targetSlot != -1) {
				Item oldItem = equipment.swapItem(targetSlot, equipItem);
				boolean canWear = true;
				
				if(is2h) {
					int otherSlot = targetSlot==Equipment.WEAPON?Equipment.SHIELD:Equipment.WEAPON;
					Item otherItem = equipment.getItemInSlot(otherSlot);
					
					if(otherItem != null) {
						if(!inventory.addItem(otherItem)) {
							p.sendMessage("You don't have enough inventory space to wear this item.");
							canWear = false;
						}
					}
				}
				
				if(canWear) {
					if(targetSlot == Equipment.WEAPON || targetSlot == Equipment.SHIELD) {
						WeaponDefinition def = Engine.items.getHandhelds(itemId);
						p.setIdleAnimation(def.getIdleAnim());
						p.getCombat().setAttackAnim(def.getAttackAnim());
						p.getCombat().setBlockAnim(def.getBlockAnim());
						int styleTab = def.getStyletab();
						p.setTabInterface(73, styleTab);
						p.sendPacket(new SendString(Engine.items.getDefinition(def.getItemId()).getName(), styleTab, 0));
					}
					inventory.setItem(slot, oldItem);
					
					p.playSound(Engine.items.getEquipment(itemId).getEquipSound());
					p.refreshInventory();
					p.refreshEquipment();
					p.appearanceUpdated = true;
					//TODO: Set equipment bonuses
					p.getCombat().setAttacking(null);
				}
			}else {
				p.sendMessage("You are unable to wear this item for some reason! "+itemId);
			}
		}
	}

}
