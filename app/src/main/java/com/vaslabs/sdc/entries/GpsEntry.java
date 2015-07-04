package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class GpsEntry extends Entry {
    private double latitude;
    private double longitude;

    private GpsEntry() {

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
