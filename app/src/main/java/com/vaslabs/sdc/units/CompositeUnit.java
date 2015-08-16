package com.vaslabs.sdc.units;


/**
 * Created by vnicolaou on 07/08/15.
 */
public class CompositeUnit {

    public final DistanceUnit DISTANCE_UNIT;
    public final TimeUnit TIME_UNIT;

    public final double DISTANCE_VALUE;

    public CompositeUnit(DistanceUnit distance_unit, TimeUnit time_unit, double distance_value) {
        DISTANCE_UNIT = distance_unit;
        TIME_UNIT = time_unit;
        DISTANCE_VALUE = distance_value;
    }

    public CompositeUnit convert(DistanceUnit distance_unit, TimeUnit time_unit) {

        if (distance_unit == DISTANCE_UNIT && TIME_UNIT == time_unit)
            return this;

        double newTimeUnitWorthOfCurrentTimeUnit = 1/time_unit.convert(1, TIME_UNIT);

        double newTotalDistance = DISTANCE_VALUE*newTimeUnitWorthOfCurrentTimeUnit;

        double newDistanceValue = distance_unit.convert(DISTANCE_UNIT, newTotalDistance);

        return new CompositeUnit(distance_unit, time_unit, newDistanceValue);

    }


    public String getMetricSignature() {
        return DISTANCE_UNIT.signature + "/" + TIME_UNIT.signature;
    }

    public String toString() {
        return String.format("%.2f%s",DISTANCE_VALUE, getMetricSignature());
    }

}
