package com.vaslabs.sdc.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author Vasilis Nicolaou
 * 
 */
public class BarometerSensor extends SDSensor<HPASensorValue>  {

    
    private HPASensorValue value;
    private HPASensorValue seaLevelPressureValue;
    private HPASensorValue groundPressureValue = null;
    private BarometerListener listener = null;
    private static BarometerSensor barometerSensor = null;
    private final static Object barometerInitLock = new Object();
    public final boolean isDummy;
    private BarometerSensor( Context c ) {

        super(Sensor.TYPE_PRESSURE, c);

        SensorManager sm = SDSensorManager.getInstance();
        
        Sensor hwSensor = getSensor();

        if ( hwSensor == null ) {
            isDummy = true;
        } else
            isDummy = false;
        seaLevelPressureValue = new HPASensorValue(SensorManager.PRESSURE_STANDARD_ATMOSPHERE);

        if (!isDummy) {
            sm.registerListener( this, hwSensor, SensorManager.SENSOR_DELAY_NORMAL );
        }
    }

    public static BarometerSensor getInstance(Context c) {
        if (barometerSensor != null)
            return barometerSensor;
        synchronized (barometerInitLock) {
            if (barometerSensor != null)
                return barometerSensor;

            BarometerSensor bs = new BarometerSensor(c);
            barometerSensor = bs;
        }
        return barometerSensor;
    }

    public void calibrate( float seaLevel ) {
        this.seaLevelPressureValue = new HPASensorValue( seaLevel );
    }
    
    public void registerListener(BarometerListener listener) {
        this.listener = listener;
    }

    @Override
    public HPASensorValue getValue() {
        return value;
    }

    public MetersSensorValue getAltitude() {
        float meters =
                SensorManager.getAltitude( seaLevelPressureValue.getRawValue(),
                        value.getRawValue() );
        MetersSensorValue altitudeValue = new MetersSensorValue( meters );
        return altitudeValue;
    }

    public MetersSensorValue getDeltaAltitude() {
        float groundPressure = groundPressureValue == null ? seaLevelPressureValue.getRawValue() : groundPressureValue.getRawValue();
        float meters = SensorManager.getAltitude(groundPressure, value.getRawValue());
        MetersSensorValue deltaAltitudeValue = new MetersSensorValue(meters);
        return deltaAltitudeValue;
    }

    @Override
    public void onAccuracyChanged( Sensor arg0, int arg1 ) {
        String accuracy = "Uknown";
        switch (arg1) {
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracy = "High"; break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracy = "Medium"; break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracy= "Low"; break;
        }
        Log.i("BarometerAccuracy", accuracy);
    }

    @Override
    public void onSensorChanged( SensorEvent sensorEvent ) {
        float[] sensorValues = sensorEvent.values;
        if ( sensorValues.length == 0 )
            return;
        if ( value == null ) {
            value = new HPASensorValue(0);
        }

        value = new HPASensorValue(sensorValues[0]);
        if (this.groundPressureValue == null) {
            this.groundPressureValue = new HPASensorValue(sensorValues[0]);
        }
        if (listener != null) {
            listener.onHPASensorValueChange( value, getAltitude(), getDeltaAltitude() );
        }

    }

    @Override
    public void finalize() {
        SensorManager sm = SDSensorManager.getInstance();
        sm.unregisterListener( this );
    }

}
