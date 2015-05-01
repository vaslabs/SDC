package com.vaslabs.sdc.logs;

import java.util.Map;
import java.util.HashMap;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LocationSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;

import java.util.HashMap;

/**
 * Created by vasilis on 04/04/2015.
 */
public final class PositionGraph {
    //private Map<Long, HPASensorValue> barometerPressureValues;
    private Map<Long, MetersSensorValue> barometerAltitudeValues;
    private Map<Long, LocationSensorValue> gpsValues;
    public static final String LOG_FILE = "PositionGraph.log";
    public PositionGraph() {
        barometerAltitudeValues = new HashMap<Long, MetersSensorValue>();
        //barometerPressureValues = new HashMap<Long, HPASensorValue>();
        gpsValues = new HashMap<Long, LocationSensorValue>();
    }

    public synchronized void registerBarometerValue(HPASensorValue pressure, MetersSensorValue altitude) {
        long now = System.currentTimeMillis();
        barometerAltitudeValues.put(now, altitude);
        //barometerPressureValues.put(now, pressure);
    }



    /**
        data are returned in a byte representation format, first
     8 bytes represent the time in milliseconds where the barometer value was register,
     next 4 bytes is the double representation of the altitude value of the barometer at that
     point of time
     */
    public synchronized byte[] getBarometerData() {
        byte[] data = new byte[barometerAltitudeValues.size()*4*8];
        int index = 0;
        byte nextByte;
        float sensorValue;
        long ts;
        for (Long timestamp : barometerAltitudeValues.keySet()) {
            ts = timestamp;
            for (int i = 0; i < 8; i++) {
                nextByte = (byte) (ts & 0xff);
                data[index++] = nextByte;
                ts = ts >>> 8;
            }
            sensorValue = barometerAltitudeValues.get(timestamp).getRawValue();
            int sensorValueBits = Float.floatToIntBits(sensorValue);
            for (int i = 0; i < 4; i++) {
                nextByte = (byte) (sensorValueBits & 0xff);
                data[index++] = nextByte;
                sensorValueBits = sensorValueBits >> 8;
            }
        }
        return data;
    }


}
