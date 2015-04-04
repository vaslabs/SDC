package com.vaslabs.sdc.logs;

import java.util.Map;
import java.util.HashMap;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LocationSensorValue;

import java.util.HashMap;

/**
 * Created by vasilis on 04/04/2015.
 */
public final class PositionGraph {
    private Map<Long, HPASensorValue> barometerValues;
    private Map<Long, LocationSensorValue> gpsValues;
    public PositionGraph() {
        barometerValues = new HashMap<Long, HPASensorValue>();
        gpsValues = new HashMap<Long, LocationSensorValue>();
    }

    public synchronized void registerValue(HPASensorValue sensorValue) {
        barometerValues.put(System.currentTimeMillis(), sensorValue);
    }


    /**
        data are returned in a byte representation format, first
     8 bytes represent the time in milliseconds where the barometer value was register,
     next 4 bytes is the double representation of the hPa value of the barometer at that
     point of time
     */
    public synchronized byte[] getBarometerData() {
        byte[] data = new byte[barometerValues.size()*4*8];
        int index = 0;
        byte nextByte;
        float sensorValue;
        long ts;
        for (Long timestamp : barometerValues.keySet()) {
            ts = timestamp;
            for (int i = 0; i < 8; i++) {
                nextByte = (byte) (ts & 0xff);
                data[index++] = nextByte;
                ts = ts >>> 8;
            }
            sensorValue = barometerValues.get(timestamp).getRawValue();
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
