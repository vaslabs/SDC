package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;

public interface SkyDiverPersonalUpdates {
    void onAltitudeUpdate(MetersSensorValue hpa);
    void onGPSUpdate(LatitudeSensorValue lat, LongitudeSensorValue lon);
}
