package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class BarometerEntry extends Entry {
    private float altitude;

    public BarometerEntry(long timestamp, float altitude) {
        super(timestamp);
        this.altitude = altitude;
    }

    public float getAltitude() {
        return altitude;
    }

}
