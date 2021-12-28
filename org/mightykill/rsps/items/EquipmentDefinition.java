package org.mightykill.rsps.items;

public class EquipmentDefinition {
	
	private int itemId;
	private int equipId = -1, equipSlot = -1;
	private int covering = 0;
	private int equipSound = 2238, unequipSound = 2244;
	private int blockSound;
	
	public EquipmentDefinition(int itemId, int equipId, int equipSlot, int covering, int equipSound, int unequipSound, int blockSound) {
		this.itemId = itemId;
		this.equipId = equipId;
		this.equipSlot = equipSlot;
		this.covering = covering;
		this.equipSound = equipSound;
		this.unequipSound = unequipSound;
		this.blockSound = blockSound;
	}

	public int getCovering() {
		return covering;
	}

	public int getItemId() {
		return itemId;
	}

	public int getEquipId() {
		return equipId;
	}

	public int getEquipSlot() {
		return equipSlot;
	}

	public int getEquipSound() {
		return equipSound;
	}

	public int getUnequipSound() {
		return unequipSound;
	}

	public int getBlockSound() {
		return blockSound;
	}

}
