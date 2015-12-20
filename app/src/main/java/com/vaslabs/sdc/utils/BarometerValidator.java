package com.vaslabs.sdc.utils;

import android.content.Context;

import com.vaslabs.sdc.sensors.BarometerSensor;
/**
 * Created by vnicolao on 20/06/15.
 */
public final class BarometerValidator extends AbstractValidator {

    private static BarometerValidator barometerValidator = null;
    private static final Object initLock = new Object();
    private String message = "Barometer is present";
    private String title = "Barometer";

    protected BarometerValidator(Context c) {
        super(c);
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

    @Override
    public CharSequence getMessage() {
        return this.message;
    }

    @Override
    public CharSequence getTitle() {
        return this.title;
    }


    public static IValidator getInstance(Context mContext) {
        synchronized (initLock) {
            if (barometerValidator == null)
                barometerValidator = new BarometerValidator(mContext);
        }
        return barometerValidator;
    }
}
