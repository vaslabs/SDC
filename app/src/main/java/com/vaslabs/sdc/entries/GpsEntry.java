package com.vaslabs.sdc.entries;

import com.vaslabs.sdc.logs.LatLng;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class GpsEntry extends Entry {
    private double latitude;
    private double longitude;

    private GpsEntry() {

    }

    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        if (this.getTimestamp() == newTimeStamTimestamp)
            return this;
        GpsEntry gpsEntry = new GpsEntry(newTimeStamTimestamp);
        gpsEntry.latitude = this.latitude;
        gpsEntry.longitude = this.longitude;
        return gpsEntry;
    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    private GpsEntry(Long key) {
        super(key);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static GpsEntry valueOf(Long key, LatLng latLng) {
        GpsEntry gpsEntry = new GpsEntry(key);
        gpsEntry.latitude = latLng.lat.getRawValue();
        gpsEntry.longitude = latLng.lng.getRawValue();
        return gpsEntry;
    }
}
