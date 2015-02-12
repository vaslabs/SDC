package com.vaslabs.sdc.utils;

import android.location.Location;

public class GeoUtils {

    public static double calculateDistance( Position positionA,
            Position positionB ) {
        double altAFromCenterOfTheEarth = positionA.getAlt().getRawValue() + 6370000;
        double altBFromCenterOfTheEarth = positionB.getAlt().getRawValue() + 6370000;
        double radOf90deg = 1.57079633;
        double x_A = altAFromCenterOfTheEarth*
                Math.cos( positionA.getLat().getRawValue() )*
                Math.sin( radOf90deg - positionA.getLng().getRawValue() );
        

        double x_B = altBFromCenterOfTheEarth*
                Math.cos( positionB.getLat().getRawValue() )*
                Math.sin( radOf90deg - positionB.getLng().getRawValue() );
        
        
        double y_A = altAFromCenterOfTheEarth*
                Math.sin(positionA.getLng().getRawValue())*
                Math.sin( radOf90deg - positionA.getLat().getRawValue() );
        
        double y_B = altBFromCenterOfTheEarth*
                Math.sin(positionB.getLng().getRawValue())*
                Math.sin( radOf90deg - positionB.getLat().getRawValue() );
        
        
        double z_A = altAFromCenterOfTheEarth*
                Math.cos( radOf90deg - positionA.getLat().getRawValue() );

        double z_B = altBFromCenterOfTheEarth*
                Math.cos( radOf90deg - positionB.getLat().getRawValue() );
        
        double distance = Math.sqrt( 
                    Math.pow(x_A - x_B, 2) +
                    Math.pow(y_A - y_B, 2) +
                    Math.pow( z_A - z_B, 2 )
        );
        
        
        return distance;
    }

}
