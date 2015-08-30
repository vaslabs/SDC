package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.types.DifferentiableFloat;

public class Position implements Differentiable<Position> {
    private LongitudeSensorValue lng;
    private LatitudeSensorValue lat;
    private MetersSensorValue alt;
    
    public Position(LongitudeSensorValue lng, LatitudeSensorValue lat, MetersSensorValue alt) {
        this.lng = lng;
        this.lat = lat;
        this.alt = alt;
    }

    public Position() {

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


    @Override
    public double differantiate(Position differentiable) {
        return GeoUtils.calculateDistance(this, differentiable);
    }

    @Override
    public int compareTo(Position another) {
        return 0;
    }

    @Override
    public String toString() {
        return "Position{" +
                "lng=" + lng +
                ", lat=" + lat +
                ", alt=" + alt +
                '}';
    }
}
