package com.vaslabs.sdc.sensors;

public class HPASensorValue extends SensorValue<Float> {

    public HPASensorValue(float seaLevel) {
        super(seaLevel);
    }

    @Override
    public String toString() {
        return String.valueOf( this.getRawValue() );
    }

}
