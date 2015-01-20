package com.vaslabs.sdc.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * @author Vasilis Nicolaou
 *
 */
public class BarometerSensor extends SDSensor<HPASensorValue> implements SensorEventListener {
    
    private Sensor hwSensor;
    private HPASensorValue value;
    
    public BarometerSensor(Context c) {
        
        SensorManager sm = SDSensorManager.getInstance(c);
        hwSensor = sm.getDefaultSensor( Sensor.TYPE_PRESSURE );
        if (hwSensor == null) {
            throw new NoBarometerException();
        }
        sm.registerListener( this, hwSensor, SensorManager.SENSOR_DELAY_NORMAL );
        
    }
    
    @Override
    public HPASensorValue getValue() {
        return value;
    }

    @Override
    public void onAccuracyChanged( Sensor arg0, int arg1 ) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged( SensorEvent sensorEvent ) {
        float[] sensorValues = sensorEvent.values;
        if (sensorValues.length == 0)
            return;
        if (value == null) {
            value = new HPASensorValue();
        }
        
        value.setRawValue(sensorValues[0]);
                
    }
    
    @Override
    public void finalize() {
        SensorManager sm = SDSensorManager.getInstance();
        sm.unregisterListener( this );
    }
    
}
