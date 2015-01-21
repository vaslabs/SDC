package com.vaslabs.sdc.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class SDSensor<T extends SensorValue<? extends Object>>
        implements SensorEventListener {

    private Sensor hwSensor;

    protected SDSensor( int sensorType, Context c ) {
        SensorManager sm = SDSensorManager.getInstance( c );
        sm.getDefaultSensor( Sensor.TYPE_PRESSURE );

    }

    protected abstract T getValue();

    protected Sensor getSensor() {
        return hwSensor;
    }
}
