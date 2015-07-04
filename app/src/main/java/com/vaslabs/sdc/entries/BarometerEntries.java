package com.vaslabs.sdc.entries;

import java.util.Collections;
import java.util.List;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class BarometerEntries {
    private List<BarometerEntry> barometerEntries;

    public BarometerEntry get(int i) {
        return barometerEntries.get(i);
    }

    public int size() {
        return barometerEntries.size();
    }

    public void sort() {
        Collections.sort(barometerEntries);
    }
}
