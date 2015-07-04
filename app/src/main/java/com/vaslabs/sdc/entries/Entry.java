package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 04/07/15.
 */
public abstract class Entry implements Comparable<Entry>{
    private long timestamp;

    public Entry(long timestamp) {
        this.timestamp = timestamp;
    }

    public Entry() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Entry entry) {
        long result = timestamp - entry.getTimestamp();
        if (result < 0)
            return -1;
        if (result > 0)
            return 1;
        return 0;
    }
}
