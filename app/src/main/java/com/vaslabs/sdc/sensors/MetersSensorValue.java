package com.vaslabs.sdc.sensors;

public final class MetersSensorValue extends SensorValue<Float> {

    public MetersSensorValue(Float value) {
        super(value);
    }

    @Override
    public String toString() {
        return String.format( "%.2f", this.getRawValue() );
    }

}
