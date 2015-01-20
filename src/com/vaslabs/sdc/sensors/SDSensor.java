package com.vaslabs.sdc.sensors;

public abstract class SDSensor<T extends SensorValue> {

    protected abstract T getValue();

}
