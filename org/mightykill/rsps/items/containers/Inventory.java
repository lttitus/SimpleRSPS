package org.mightykill.rsps.items.containers;

import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public class Inventory extends Container {
	
	public static final int MAX_SIZE = 28;
	
	public Inventory(int size, Item[] initItems) {
		super(size, initItems);
	}

	public Inventory(Item[] initItems) {
		this(initItems.length, initItems);
	}
	
	public Inventory() {
		this(new Item[MAX_SIZE]);
	}

}
