package com.vaslabs.sdc.sensors;

import android.content.Context;
import android.hardware.SensorManager;

/**
 * Facade for managing sensors
 * @author Vasilis Nicolaou
 *
 */
public class SDSensorManager {
    Context context;
    private static SensorManager sensorManager;
    private static SDSensorManager sdSensorManager;
    
    private SDSensorManager() {
        sensorManager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );
    }
    
    public static SensorManager getInstance() {
        if (sdSensorManager == null) {
            sdSensorManager = new SDSensorManager();
        }
        return sensorManager;
    }
    
}
