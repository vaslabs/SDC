package com.vaslabs.sdc.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 
 * @author Vasilis Nicolaou
 * 
 */
public class GPSSensor implements LocationListener {
    private Location location;
    private static final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    private LocationManager locationManager;
    public GPSSensor( Context context ) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, 0, 0, this);

        location = locationManager.getLastKnownLocation( LOCATION_PROVIDER );
    }

    
    public Location getCurrentLocation() {

        return location;
    }


    @Override
    public void onLocationChanged( Location l ) {
        location = l;
    }


    @Override
    public void onProviderDisabled( String arg0 ) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onProviderEnabled( String arg0 ) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onStatusChanged( String arg0, int arg1, Bundle arg2 ) {
        // TODO Auto-generated method stub   
    }
    
    public void finalise() {
        locationManager.removeUpdates(this);
    }

}
