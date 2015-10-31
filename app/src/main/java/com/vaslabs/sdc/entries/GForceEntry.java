package com.vaslabs.sdc.entries;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class GForceEntry extends Entry{

    public final float gForceValue;

    public GForceEntry(long timestamp, float value) {
        super(timestamp);
        this.gForceValue = value;
    }

    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        return new GForceEntry(newTimeStamTimestamp, gForceValue);
    }

    @Override
    public float getX() {
        return this.getTimestamp()/1000f;
    }

    @Override
    public float getY() {
        return gForceValue;
    }
}
