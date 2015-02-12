package com.vaslabs.sdc.utils;

import android.location.Location;

public class GeoUtils {

    public static double calculateDistance( Position positionA,
            Position positionB ) {
        double altAFromCenterOfTheEarth =
                positionA.getAlt().getRawValue() + 6370000;
        double altBFromCenterOfTheEarth =
                positionB.getAlt().getRawValue() + 6370000;
        double x_A =
                altAFromCenterOfTheEarth
                        * Math.cos( Math.toRadians( positionA.getLng()
                                .getRawValue() ) )
                        * Math.sin( Math.toRadians( 90 - positionA.getLat()
                                .getRawValue() ) );

        double x_B =
                altBFromCenterOfTheEarth
                        * Math.cos( Math.toRadians( positionB.getLng()
                                .getRawValue() ) )
                        * Math.sin( Math.toRadians( 90 - positionB.getLat()
                                .getRawValue() ) );

        double y_A =
                altAFromCenterOfTheEarth
                        * Math.sin( Math.toRadians( positionA.getLng()
                                .getRawValue() ) )
                        * Math.sin( Math.toRadians( 90 - positionA.getLat()
                                .getRawValue() ) );

        double y_B =
                altBFromCenterOfTheEarth
                        * Math.sin( Math.toRadians( positionB.getLng()
                                .getRawValue() ) )
                        * Math.sin( Math.toRadians( 90 - positionB.getLat()
                                .getRawValue() ) );

        double z_A =
                altAFromCenterOfTheEarth
                        * Math.cos( Math.toRadians( 90 - positionA.getLat()
                                .getRawValue() ) );

        double z_B =
                altBFromCenterOfTheEarth
                        * Math.cos( Math.toRadians( 90 - positionB.getLat()
                                .getRawValue() ) );

        double distance =
                Math.sqrt( Math.pow( x_A - x_B, 2 ) + Math.pow( y_A - y_B, 2 )
                        + Math.pow( z_A - z_B, 2 ) );

        return distance;
    }

}
