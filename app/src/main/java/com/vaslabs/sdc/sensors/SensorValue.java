package com.vaslabs.sdc.sensors;

public abstract class SensorValue<V> {

    private V raw_value;

    public SensorValue(V value) {
        raw_value = value;
    }

    public V getRawValue() {
        return raw_value;
    }
    
    public abstract String toString();
}
