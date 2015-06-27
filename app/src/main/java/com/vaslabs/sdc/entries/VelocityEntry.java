package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 27/06/15.
 */
public final class VelocityEntry {
    public final long timestamp;
    public final float velocity;

    public VelocityEntry(long timestamp, float velocity) {
        this.timestamp = timestamp;
        this.velocity = velocity;
    }
}
