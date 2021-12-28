package org.mightykill.rsps.items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.mightykill.rsps.items.useresults.ItemOnItemResult;

public class ItemManager {
	
	private Connection sql;
	private HashMap<Integer, ItemDefinition> cachedDefinitions = new HashMap<Integer, ItemDefinition>();
	private HashMap<Integer, EquipmentDefinition> cachedEquipment = new HashMap<Integer, EquipmentDefinition>();
	private HashMap<Integer, WeaponDefinition> cachedWeapons = new HashMap<Integer, WeaponDefinition>();
	
	public ItemManager(Connection sqlConnection) {
		this.sql = sqlConnection;
	}
	
	public ItemDefinition getDefinition(int itemId) {
		ItemDefinition def = cachedDefinitions.get(itemId);
		
		/* Request definition from SQL server */
		if(def == null) {	//Must always produce a result, even if broken
			try {
				PreparedStatement ps = sql.prepareStatement("SELECT * FROM items WHERE id=?");
				ps.setInt(1, itemId);
				if(ps.execute()) {
					ResultSet result = ps.getResultSet();
					result.next();
					def = new ItemDefinition(itemId, 
							result.getString("name"),
							result.getString("examine"), 
							result.getBoolean("canstack"), 
							result.getBoolean("isnote"),
							result.getBoolean("cannote"), 
							result.getBoolean("canequip"));
					
					cachedDefinitions.put(itemId, def);
					System.out.println("Cached "+itemId);
				}
			} catch (SQLException e) {
				System.err.println("Item Id not found: "+itemId);
				def = new ItemDefinition(itemId, "Undefined item", "Item not defined! "+itemId, false, false, false, false);
			}
		}
		
		return def;
	}
	
	public EquipmentDefinition getEquipment(int itemId) {
		EquipmentDefinition def = cachedEquipment.get(itemId);
		
		if(def == null) {
			try {
				PreparedStatement ps = sql.prepareStatement("SELECT * FROM equipment WHERE itemid=?");
				ps.setInt(1, itemId);
				if(ps.execute()) {
					ResultSet result = ps.getResultSet();
					result.next();
					def = new EquipmentDefinition(itemId, 
							result.getInt("equipid"),
							result.getInt("equipslot"),
							result.getInt("covering"),
							result.getInt("equipsound"),
							result.getInt("unequipsound"),
							result.getInt("blocksound"));
					
					cachedEquipment.put(itemId, def);
					System.out.println("Equipment Cached "+itemId);
				}
			} catch (SQLException e) {
				System.err.println("Equipment Item Id not found: "+itemId);
				def = new EquipmentDefinition(itemId, -1, -1, -1, -1, -1, -1);
			}
		}
		
		return def;
	}
	
	public WeaponDefinition getHandhelds(int itemId) {
		WeaponDefinition def = cachedWeapons.get(itemId);
		
		if(def == null) {
			try {
				PreparedStatement ps = sql.prepareStatement("SELECT * FROM handhelds WHERE itemid=?");
				ps.setInt(1, itemId);
				if(ps.execute()) {
					ResultSet result = ps.getResultSet();
					result.next();
					def = new WeaponDefinition(
							result.getInt("itemid"),
							result.getInt("style"),
							result.getBoolean("istwohanded"),
							result.getBoolean("hasspec"),
							result.getInt("idleanim"),
							result.getInt("attackanim"),
							result.getInt("blockanim"),
							result.getInt("hitsound"),
							result.getInt("cooldown"),
							result.getInt("styletab"));
					
					cachedWeapons.put(itemId, def);
					System.out.println("Handheld Cached "+itemId);
				}
			} catch (SQLException e) {
				System.err.println("Handheld Item Id not found: "+itemId);
				def = new WeaponDefinition(itemId, 0, false, false, 808, 422, 424, 511, 4, 82);
			}
		}
		
		return def;
	}
	
	public ItemOnItemResult checkItemOnItem(int usedItem, int usedWith, boolean reverse) {
		try {
			int primaryId = reverse?usedWith:usedItem;
			int secondaryId = reverse?usedItem:usedWith;
			PreparedStatement ps = sql.prepareStatement("SELECT usedresult, withresult, skill FROM itemonitem WHERE usedid=? AND withid=?");
			ps.setInt(1, primaryId);
			ps.setInt(2, secondaryId);
			
			if(ps.execute()) {	//Item on Item combination found
				ResultSet result = ps.getResultSet();
				result.next();
				int primaryResult = reverse?result.getInt("withresult"):result.getInt("usedresult");
				int secondaryResult = reverse?result.getInt("usedresult"):result.getInt("withresult");
				
				return new ItemOnItemResult(primaryId, secondaryId, primaryResult, secondaryResult, result.getInt("skill"));
			}else {	//Combination not found
				if(!reverse) {	//Try to reverse the Item Id if we haven't already
					return checkItemOnItem(usedItem, usedWith, true);
				}	//If still not found, error + return null ("Nothing interesting happens")
				
				System.err.println("Item on Item combination not found: "+usedItem+" + "+usedWith);
			}
		} catch (SQLException e) {
			System.err.println("Item on Item combination not found: "+usedItem+" + "+usedWith);
		}
		
		return null;
	}

	public void clearCache() {
		cachedDefinitions.clear();
		cachedEquipment.clear();
		cachedWeapons.clear();
	}

}
