package com.vaslabs.sdc.entries;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class AccelerationEntry extends Entry {
    public final float acceleration;

    public AccelerationEntry(long timestamp, float acceleration) {
        super(timestamp);
        this.acceleration = acceleration;
    }

    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        return new AccelerationEntry(newTimeStamTimestamp, acceleration);
    }

    @Override
    public float getX() {
        return getTimestamp()/1000f;
    }

    @Override
    public float getY() {
        return acceleration;
    }
}
