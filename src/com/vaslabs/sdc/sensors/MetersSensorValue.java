package com.vaslabs.sdc.sensors;

public class MetersSensorValue extends SensorValue<Float> {

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return String.format( "%2.f", this.getRawValue() );
    }

}
