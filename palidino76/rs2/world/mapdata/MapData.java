/*
 * Class MapData
 *
 * Version 1.0
 *
 * Saturday, August 16, 2008
 *
 * Created by Palidino76
 */

package palidino76.rs2.world.mapdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.mightykill.rsps.util.Misc;

public class MapData {
    /**
     * Each list contains the region and 4 pieces of data.
     */
    public MapList[] mapLists = new MapList[2000];

    /**
     * Constructs a new MapData class.
     */
    public MapData() {
        loadMapAreaData();
    }

    /**
     * Load the map data into memory for faster load time.
     */
    public void loadMapAreaData() {
        int curId = 0;
        for (int i = 0; i < 16000; i++) {
            try {
                File file = new File("./data/mapdata/" + i + ".txt");
                if (file.exists()) {
                    MapList list = mapLists[curId++] = new MapList();
                    BufferedReader in = new BufferedReader(new FileReader("./data/mapdata/" + i + ".txt"));
                    String str;
                    int regionId = 0;
                    list.region = i;
                    while ((str = in.readLine()) != null) {
                        if (!str.equals("")) {
                            list.data[regionId++] = Integer.parseInt(str.trim());
                        }
                    }
                    in.close();
                    in = null;
                }
                file = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the four pieces of map data from a region.
     * @param myRegion The region to get data from.
     * @return Returns the four mapdata.
     */
    public int[] getData(int myRegion) {
        for (MapList list : mapLists) {
            if (list == null) {
                continue;
            }
            if (list.region == myRegion) {
                return list.data;
            }
        }
        Misc.println("Missing map data: " + myRegion);
        return new int[4];
    }
}