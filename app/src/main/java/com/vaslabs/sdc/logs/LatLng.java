package com.vaslabs.sdc.logs;

import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class LatLng {
    public final LatitudeSensorValue lat;
    public final LongitudeSensorValue lng;

    public LatLng(LatitudeSensorValue lat, LongitudeSensorValue lng) {
        this.lat = lat;
        this.lng = lng;
    }
}