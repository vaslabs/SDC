package com.vaslabs.sdc.logs;

import android.location.Location;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LocationSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;

import java.util.HashMap;

/**
 * Created by vasilis on 04/04/2015.
 */
public final class PositionGraph {
    //private Map<Long, HPASensorValue> barometerPressureValues;
    private Map<Long, MetersSensorValue> barometerAltitudeValues;
    private Map<Long, LatLng> gpsValues;
    public static final String BAROMETER_LOG_FILE = "PositionGraphBarometer.log";
    public static final String GPS_LOG_FILE = "PositionGraphGPS.log";
    private float lastValue = -1000;
    public PositionGraph() {
        barometerAltitudeValues = new HashMap<Long, MetersSensorValue>();
        //barometerPressureValues = new HashMap<Long, HPASensorValue>();
        gpsValues = new HashMap<Long, LatLng>();
    }

    public synchronized void registerBarometerValue(HPASensorValue pressure, MetersSensorValue altitude) {

        if (altitude == null)
            return;
        if (Math.abs(altitude.getRawValue() - lastValue) < 3) {
            return;
        }
        lastValue = altitude.getRawValue();
        long now = System.currentTimeMillis();

        barometerAltitudeValues.put(now, altitude);
        //barometerPressureValues.put(now, pressure);
    }

    public synchronized void registerGPSValue(LatitudeSensorValue lat, LongitudeSensorValue lng) {
        long now = System.currentTimeMillis();
        gpsValues.put(now, new LatLng(lat, lng));

    }



    /**
        data are returned in a byte representation format, first
     8 bytes represent the time in milliseconds where the barometer value was register,
     next 4 bytes is the double representation of the altitude value of the barometer at that
     point of time
     */
    public synchronized byte[] getBarometerData() {
        byte[] data = new byte[barometerAltitudeValues.size()*12];
        int index = 0;
        byte nextByte;
        float sensorValue;
        long ts;
        ByteBuffer bf = ByteBuffer.wrap(data);
        for (Long timestamp : barometerAltitudeValues.keySet()) {
            bf.putLong(timestamp);
            sensorValue = barometerAltitudeValues.get(timestamp).getRawValue();
            bf.putFloat(sensorValue);
        }
        return data;
    }

    public synchronized byte[] getGPSData() {
        byte[] data = new byte[gpsValues.size()*24];
        ByteBuffer bf = ByteBuffer.wrap(data);

        int index = 0;
        byte nextByte;
        double sensorValue;
        long ts;
        for (Long timestamp : gpsValues.keySet()) {
            bf.putLong(timestamp);
            sensorValue = gpsValues.get(timestamp).lat.getRawValue();
            bf.putDouble(sensorValue);
            sensorValue = gpsValues.get(timestamp).lng.getRawValue();
            bf.putDouble(sensorValue);
        }
        return data;
    }


}

class LatLng {
    public final LatitudeSensorValue lat;
    public final LongitudeSensorValue lng;

    public LatLng(LatitudeSensorValue lat, LongitudeSensorValue lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
