package com.vaslabs.sdc.sensors;

public class HPASensorValue extends SensorValue<Float> {

    @Override
    public String toString() {
        return String.valueOf( this.getRawValue() );
    }

}
