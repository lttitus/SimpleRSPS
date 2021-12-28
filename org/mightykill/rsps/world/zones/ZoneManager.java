package org.mightykill.rsps.world.zones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.mightykill.rsps.entities.Entity;

public class ZoneManager {
	
	private Connection sql;
	
	private ArrayList<Zone> zones = new ArrayList<Zone>();
	
	public ZoneManager(Connection sqlConnection) {
		this.sql = sqlConnection;
		
		System.out.println("Loaded "+loadZones()+" zones.");
	}
	
	public int loadZones() {
		zones.clear();
		int loaded = 0;
		
		try {
			PreparedStatement ps = sql.prepareStatement("SELECT * FROM zones");
			
			if(ps.execute()) {
				ResultSet result = ps.getResultSet();
				
				while(result.next()) {
					zones.add(new Zone(
							result.getInt("id"),
							result.getInt("boundx1"),
							result.getInt("boundy1"),
							result.getInt("boundx2"),
							result.getInt("boundy2"),
							result.getBoolean("ispvp"),
							result.getBoolean("ismulti")));
					
					loaded++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return loaded;
	}
	
	public ArrayList<Zone> getApplicableZones(Entity e) {
		ArrayList<Zone> results = new ArrayList<Zone>();
		
		for(Zone z:zones) {
			if(z.withinBounds(e)) {
				results.add(z);
			}
		}
		
		return results;
	}

}
