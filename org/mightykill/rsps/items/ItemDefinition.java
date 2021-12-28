package org.mightykill.rsps.items;

public class ItemDefinition {
	
	private int itemId;
	private String name, examine;
	private boolean isStackable, isNoted, canNote, isEquipable;
	
	public ItemDefinition(int id, String name, String examine, boolean isStackable, boolean isNoted, boolean canNote, boolean isEquipable) {
		this.itemId = id;
		this.name = name;
		this.examine = examine;
		this.isStackable = isStackable;
		this.canNote = canNote;
		this.isNoted = isNoted;
		this.isEquipable = isEquipable;
	}

	public String getName() {
		return name;
	}

	public String getExamine() {
		return examine;
	}

	public boolean isStackable() {
		return isStackable;
	}

	public boolean isNoted() {
		return isNoted;
	}
	
	public boolean canNote() {
		return this.canNote;
	}

	public boolean isEquipable() {
		return isEquipable;
	}

	public int getItemId() {
		return itemId;
	}

}
