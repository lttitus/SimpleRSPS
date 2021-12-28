package org.mightykill.rsps.items.containers;

import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public class Equipment extends Container {
	
	public static final int HELMET = 0;
    public static final int CAPE = 1;
    public static final int AMULET = 2;
    public static final int WEAPON = 3;
    public static final int CHEST = 4;
    public static final int SHIELD = 5;
    public static final int LEGS = 7;
    public static final int HANDS = 9;
    public static final int FEET = 10;
    public static final int RING = 12;
    public static final int AMMO = 13;

	public Equipment(Item[] initItems) {
		super(initItems);
	}
	
	public Equipment() {
		super(new Item[14]);
	}
	
	public int equipItem(Item item) {
		return -1;
	}
	
	public Item swapItem(int targetSlot, Item item) {
		if(targetSlot > items.length) return null;
		Item oldItem = items[targetSlot];
		
		items[targetSlot] = item;
		
		return oldItem;
	}

}
