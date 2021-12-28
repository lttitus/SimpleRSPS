package org.mightykill.rsps.io.packets.outgoing;

public class UpdateGEOffer extends OutgoingPacket {

	public UpdateGEOffer(int slot, int progress, int itemId, int price, int itemAmount, int numSold, int totalAmount) {
		super(137, 20);
		addByte(slot);		//Slot
		addByte(progress);	//1 = Submitting..., 2 - 4 = In Progress, 5 = Complete or Aborted, depending on sold/total below, 6-7 = In Progress
		addShort(itemId);	//Item Id
		addInt(price);		//Amount of money, "bought/sold x item for <price> gp"
		addInt(itemAmount);	//Item amount
		addInt(numSold);	//# Sold
		addInt(totalAmount);//Total
	}

}
