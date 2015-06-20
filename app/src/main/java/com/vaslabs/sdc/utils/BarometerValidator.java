package com.vaslabs.sdc.utils;

import android.content.Context;

import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.SDSensorManager;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class BarometerValidator implements IValidator {

    private final Context mContext;
    public BarometerValidator(Context c) {
        this.mContext = c;
    }

    @Override
    public boolean validate() {
        try {
            BarometerSensor sensor = BarometerSensor.getInstance(this.mContext);
            return !sensor.isDummy;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ValidationMessageType getMessageType() {
        return ValidationMessageType.WARNING;
    }
}
