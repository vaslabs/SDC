package com.vaslabs.sdc.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class GpsEntries {

    private List<GpsEntry> gpsEntries;

    public GpsEntries() {

    }

    public GpsEntries(List<GpsEntry> gpsEntries) {
        this.gpsEntries = new ArrayList<>(gpsEntries);
    }


    public int size() {
        return gpsEntries.size();
    }

    public GpsEntry getEntry(int i) {
        return gpsEntries.get(i);
    }

    public void sort() {
        Collections.sort(gpsEntries);
    }
}
