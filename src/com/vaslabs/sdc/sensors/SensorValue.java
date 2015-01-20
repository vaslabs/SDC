package com.vaslabs.sdc.sensors;

public abstract class SensorValue {
    
    private float raw_value;
    private boolean isInitialised = false;
    
    public boolean hasBeenInitialised() {
        return isInitialised;
    }
    
    public void setRawValue(float value) {
        raw_value = value;
        isInitialised = true;
    }

    public float getRawValue() {
        return raw_value;
    }
}
