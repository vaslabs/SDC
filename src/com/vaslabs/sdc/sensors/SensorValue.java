package com.vaslabs.sdc.sensors;

public abstract class SensorValue {
    
    private double raw_value;
    private boolean isInitialised = false;
    
    public boolean hasBeenInitialised() {
        return isInitialised;
    }
    
    public void setRawValue(double value) {
        raw_value = value;
        isInitialised = true;
    }

    public double getRawValue() {
        return raw_value;
    }
}
