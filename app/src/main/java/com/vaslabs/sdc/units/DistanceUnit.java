package com.vaslabs.sdc.units;

/**
 * Created by vnicolaou on 07/08/15.
 */
public enum DistanceUnit {
    METERS("m") {
        @Override
        public double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toMeters(distance_value);
        }

        @Override
        public double toKm(double m) {
            return m/CKM;
        }

        @Override
        public double toFeet(double m) {
            return m*CF;
        }

        @Override
        public double toMeters(double m) {
            return m;
        }

    }, FEET("ft") {
        @Override
        public double toKm(double ft) {
            return (ft/(CF*CKM));
        }

        @Override
        public double toFeet(double f) {
            return f;
        }

        @Override
        public double toMeters(double ft) {
            return ft/CF;
        }

        @Override
        public double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toFeet(distance_value);
        }
    }, KM("km") {
        @Override
        public double toKm(double km) {
            return km;
        }

        @Override
        public double toFeet(double km) {
            return km*CKM*CF;
        }

        @Override
        public double toMeters(double km) {
            return km*CKM;
        }

        @Override
        public double convert(DistanceUnit distanceUnit, double distance_value) {
            return distanceUnit.toKm(distance_value);
        }
    };

    public abstract double toKm(double km);

    public final double CF = 3.2808;
    public final double CKM = 1000;

    public abstract double toFeet(double m);

    public abstract double toMeters(double distance_value);

    public final String signature;

    DistanceUnit(String signature) {
        this.signature = signature;
    }

    public abstract double convert(DistanceUnit distanceUnit, double distance_value);

    public String toString(double value) {
        return String.format("%.2f%s", value, signature);
    }

}
