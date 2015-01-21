package com.vaslabs.sdc.sensors;

public abstract class SensorValue<V> {

    private V raw_value;
    private boolean isInitialised = false;

    public boolean hasBeenInitialised() {
        return isInitialised;
    }

    public void setRawValue( V value ) {
        raw_value = value;
        isInitialised = true;
    }

    public V getRawValue() {
        return raw_value;
    }
}
