package org.mightykill.rsps.items.useresults;

public class ItemOnItemResult extends ItemUseResult {
	
	protected int withItem, withResult;
	protected int skillId;

	public ItemOnItemResult(int usedItem, int withItem, int usedResult, int withResult, int skillId) {
		super(usedItem, usedResult);
		this.withItem = withItem;
		this.withResult = withResult;
		this.skillId = skillId;
	}
	
	public int getUsedWith() {
		return this.withItem;
	}
	
	public int getUsedWithResult() {
		return this.withResult;
	}
	
	public int getSkillId() {
		return this.skillId;
	}

}
