package com.vaslabs.sdc.entries;

import java.util.List;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class GpsEntries {

    private List<GpsEntry> gpsEntries;


    public int size() {
        return gpsEntries.size();
    }

    public GpsEntry getEntry(int i) {
        return gpsEntries.get(i);
    }
}
