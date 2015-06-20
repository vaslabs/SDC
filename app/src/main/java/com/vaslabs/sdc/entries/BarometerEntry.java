package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class BarometerEntry implements Comparable<BarometerEntry> {
    private long timestamp;
    private float altitude;

    public BarometerEntry(long timestamp, float altitude) {
        this.timestamp = timestamp;
        this.altitude = altitude;
    }

    @Override
    public int compareTo(BarometerEntry barometerEntry) {
        long result = timestamp - barometerEntry.getTimestamp();
        if (result < 0)
            return -1;
        if (result > 0)
            return 1;
        return 0;
    }

    public float getAltitude() {
        return altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
