package com.vaslabs.sdc.sensors;

/**
 * Created by vnicolao on 02/05/15.
 */
public interface GPSSensorListener {
    void onLatLngChange(LatitudeSensorValue lat, LongitudeSensorValue lng);
}
