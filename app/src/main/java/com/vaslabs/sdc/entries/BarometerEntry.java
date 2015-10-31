package com.vaslabs.sdc.entries;

import com.vaslabs.sdc.sensors.MetersSensorValue;

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

    public static BarometerEntry valueOf(Long key, MetersSensorValue metersSensorValue) {
        BarometerEntry be = new BarometerEntry(key, metersSensorValue.getRawValue());
        return be;
    }

    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        if (newTimeStamTimestamp == this.getTimestamp())
            return this;
        BarometerEntry be = new BarometerEntry(newTimeStamTimestamp, this.altitude);
        return be;

    }

    @Override
    public float getX() {
        return this.getTimestamp()/1000f;
    }

    @Override
    public float getY() {
        return this.getY();
    }
}
