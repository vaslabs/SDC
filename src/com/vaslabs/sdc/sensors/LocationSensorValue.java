package com.vaslabs.sdc.sensors;

public class LocationSensorValue extends SensorValue <Double>{

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format( "%f", this.getRawValue());
    }

}
