package com.vaslabs.sdc.sensors;

public class MetersSensorValue extends SensorValue<Float> {

    @Override
    public String toString() {
        return String.format( "%.2f", this.getRawValue() );
    }

}
