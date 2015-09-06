package com.vaslabs.sdc.logs;

import android.location.Location;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LocationSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;

import java.util.HashMap;

/**
 * Created by vasilis on 04/04/2015.
 */
public final class PositionGraph {
    private static final int SEA_LEVEL = 0;
    private static final int DELTA_GROUND_LEVEL = 1;
    //private Map<Long, HPASensorValue> barometerPressureValues;
    private Map<Long, MetersSensorValue[]> barometerAltitudeValues;
    private Map<Long, LatLng> gpsValues;
    public static final String BAROMETER_LOG_FILE = "PositionGraphBarometer.log";
    public static final String GPS_LOG_FILE = "PositionGraphGPS.log";
    private float lastValue = -1000;
    private Position lastPosition = new Position();

    public PositionGraph() {
        barometerAltitudeValues = new HashMap<Long, MetersSensorValue[]>();
        //barometerPressureValues = new HashMap<Long, HPASensorValue>();
        gpsValues = new HashMap<Long, LatLng>();
    }

    public void registerBarometerValue(HPASensorValue pressure, MetersSensorValue altitude, MetersSensorValue deltaAltitude) {

        if (altitude == null)
            return;

        lastValue = altitude.getRawValue();
        long now = System.currentTimeMillis();
        MetersSensorValue[] barometerEntries = new MetersSensorValue[2];
        barometerEntries[SEA_LEVEL] =  altitude;
        barometerEntries[DELTA_GROUND_LEVEL] = deltaAltitude;
        barometerAltitudeValues.put(now, barometerEntries);
        lastPosition.setAlt(altitude);
        //barometerPressureValues.put(now, pressure);
    }

    public void registerGPSValue(LatitudeSensorValue lat, LongitudeSensorValue lng) {
        long now = System.currentTimeMillis();
        gpsValues.put(now, new LatLng(lat, lng));
        lastPosition.setLat(lat);
        lastPosition.setLng(lng);

    }



    /**
        data are returned in a byte representation format, first
     8 bytes represent the time in milliseconds where the barometer value was register,
     next 4 bytes is the double representation of the altitude value of the barometer at that
     point of time
     */
    public byte[] getBarometerData() {
        byte[] data = new byte[barometerAltitudeValues.size()*16];
        int index = 0;
        byte nextByte;
        float sensorValue;
        long ts;
        ByteBuffer bf = ByteBuffer.wrap(data);
        for (Long timestamp : barometerAltitudeValues.keySet()) {
            bf.putLong(timestamp);
            sensorValue = barometerAltitudeValues.get(timestamp)[SEA_LEVEL].getRawValue();
            bf.putFloat(sensorValue);
            sensorValue = barometerAltitudeValues.get(timestamp)[DELTA_GROUND_LEVEL].getRawValue();
            bf.putFloat(sensorValue);
        }
        return data;
    }

    public byte[] getGPSData() {
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


    public Position getLastPosition() {
        return lastPosition;
    }
}
