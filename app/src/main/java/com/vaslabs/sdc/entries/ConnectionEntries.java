package com.vaslabs.sdc.entries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class ConnectionEntries {

    private List<ConnectionEntry> connectionEntries;

    public ConnectionEntries() {

    }

    public ConnectionEntries(List<ConnectionEntry> connectionEntries) {
        this.connectionEntries = new ArrayList<>(connectionEntries);
    }

    public int size() {
        return connectionEntries.size();
    }

    public ConnectionEntry getEntry(int i) {
        return connectionEntries.get(i);
    }

    public void sort() {
        Collections.sort(connectionEntries);
    }

}
