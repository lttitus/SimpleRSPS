package org.mightykill.rsps.items.containers;

import org.mightykill.rsps.items.Container;
import org.mightykill.rsps.items.Item;

public class Inventory extends Container {
	
	public static final int MAX_SIZE = 28;

	public Inventory(Item[] initItems) {
		super(initItems);
	}
	
	public Inventory() {
		super(new Item[MAX_SIZE]);
	}

}
