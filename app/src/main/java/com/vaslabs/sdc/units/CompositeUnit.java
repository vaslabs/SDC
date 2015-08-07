package com.vaslabs.sdc.units;


/**
 * Created by vnicolaou on 07/08/15.
 */
public class CompositeUnit<D extends DistanceUnit, T extends TimeUnit> {

    public final DistanceUnit DISTANCE_UNIT;
    public final TimeUnit TIME_UNIT;

    public final double DISTANCE_VALUE;
    public final double TIME_VALUE;

    public CompositeUnit(DistanceUnit distance_unit, TimeUnit time_unit, double distance_value, double time_value) {
        DISTANCE_UNIT = distance_unit;
        TIME_UNIT = time_unit;
        DISTANCE_VALUE = distance_value;
        TIME_VALUE = time_value;
    }

    public double getValue(DistanceUnit distanceUnit, TimeUnit timeUnit) {
        double value = DISTANCE_UNIT.convert(distanceUnit, DISTANCE_VALUE);
        double time_value = TIME_UNIT.convert(TIME_VALUE, timeUnit);
        return value/time_value;
    }

    public String getMetricSignature() {
        return DISTANCE_UNIT.signature + "/" + TIME_UNIT.signature;
    }

    public String toString() {
        double calculatedDistancePerTime = DISTANCE_VALUE / TIME_VALUE;
        return calculatedDistancePerTime + getMetricSignature();
    }

}
