package com.vaslabs.sdc.units;

/**
 * Created by vnicolaou on 07/08/15.
 */
public enum DistanceUnit {
    METERS("m") {
        @Override
        double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toMeters(distance_value);
        }

        @Override
        double toKm(double m) {
            return m/CKM;
        }

        @Override
        double toFeet(double m) {
            return m*CF;
        }

        @Override
        double toMeters(double m) {
            return m;
        }

    }, FEET("ft") {
        @Override
        double toKm(double ft) {
            return (ft/(CF*CKM));
        }

        @Override
        double toFeet(double f) {
            return f;
        }

        @Override
        double toMeters(double ft) {
            return ft/CF;
        }

        @Override
        double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toFeet(distance_value);
        }
    }, KM("km") {
        @Override
        double toKm(double km) {
            return km;
        }

        @Override
        double toFeet(double km) {
            return km*CKM*CF;
        }

        @Override
        double toMeters(double distance_value) {
            return distance_value*CKM;
        }

        @Override
        double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toKm(distance_value);
        }
    };

    abstract double toKm(double km);

    public final double CF = 3.2808;
    public final double CKM = 1000;

    abstract double toFeet(double m);

    abstract double toMeters(double distance_value);

    public final String signature;

    DistanceUnit(String signature) {
        this.signature = signature;
    }

    abstract double convert(DistanceUnit distanceUnit, double distance_value);

}
