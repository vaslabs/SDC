package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LocationSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;

public class Position {
    private LongitudeSensorValue lng;
    private LatitudeSensorValue lat;
    private MetersSensorValue alt;
    
    public Position(LongitudeSensorValue lng, LatitudeSensorValue lat, MetersSensorValue alt) {
        this.lng = lng;
        this.lat = lat;
        this.alt = alt;
    }

    public LongitudeSensorValue getLng() {
        return lng;
    }

    public void setLng( LongitudeSensorValue lng ) {
        this.lng = lng;
    }

    public LatitudeSensorValue getLat() {
        return lat;
    }

    public void setLat( LatitudeSensorValue lat ) {
        this.lat = lat;
    }

    public MetersSensorValue getAlt() {
        return alt;
    }

    public void setAlt( MetersSensorValue alt ) {
        this.alt = alt;
    }
    
    
    
    
}
