package com.vaslabs.sdc.sensors;

public abstract class LocationSensorValue extends SensorValue <Double>{

    public LocationSensorValue(Double value) {
        super(value);
    }

    @Override
    public String toString() {
        return String.format( "%f", this.getRawValue());
    }

}
