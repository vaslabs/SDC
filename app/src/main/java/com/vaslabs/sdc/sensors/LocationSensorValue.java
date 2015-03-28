package com.vaslabs.sdc.sensors;

public class LocationSensorValue extends SensorValue <Double>{

    @Override
    public String toString() {
        return String.format( "%f", this.getRawValue());
    }

}
