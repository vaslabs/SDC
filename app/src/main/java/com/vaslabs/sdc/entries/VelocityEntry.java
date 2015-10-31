package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 27/06/15.
 */
public final class VelocityEntry extends Entry {
    public final float velocity;

    public VelocityEntry(long timestamp, float velocity) {
        super(timestamp);
        this.velocity = velocity;
    }

    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        return new VelocityEntry(newTimeStamTimestamp, velocity);
    }

    @Override
    public float getX() {
        return getTimestamp()/1000f;
    }

    @Override
    public float getY() {
        return velocity;
    }
}
