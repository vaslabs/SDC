package com.vaslabs.sdc.sensors;

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
    
    public BarometerSensor(Sensor sensor) {
        if (sensor.getType() == Sensor.TYPE_PRESSURE)
            this.hwSensor = sensor;
        else
            throw new NoBarometerException();
        
        SensorManager sm = SDSensorManager.getInstance();
        sm.registerListener( this, hwSensor, SensorManager.SENSOR_DELAY_NORMAL );
        
    }
    
    @Override
    protected HPASensorValue getValue() {
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
    
}
