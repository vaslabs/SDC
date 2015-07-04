package com.vaslabs.sdc.entries;

import java.util.List;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class ConnectionEntries {

    private List<ConnectionEntry> connectionEntries;

    public int size() {
        return connectionEntries.size();
    }

    public ConnectionEntry getEntry(int i) {
        return connectionEntries.get(i);
    }


}
