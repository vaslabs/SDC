package com.vaslabs.sdc.sensors;

public abstract class SDSensor<T extends SensorValue<? extends Object>> {

    protected abstract T getValue();

}
