package com.vaslabs.sdc.utils;

import android.util.Log;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;

public class SkyDiver implements PositionalUpdate {
    private UserInformation userInformation;
    private Position lastKnownPosition;
    
    public SkyDiver( UserInformation ui ) {
        userInformation = UserInformation.getUserInformationClone( ui );
    }

    public static SkyDiver serialiseSkyDiverFromString(String skydiverDeserialisedString) {
        String[] mainFields = skydiverDeserialisedString.split( ":" );
        String name, massValue, seaLevelValue, latValue, lngValue, altValue;
        try {
            name = mainFields[0];
            String valuesField = mainFields[1];
            String[] values = valuesField.split( "|" );
            
            massValue = values[0];
            seaLevelValue = values[1];
            latValue = values[2];
            lngValue = values[3];
            altValue = values[4];
            
        }
        catch (ArrayIndexOutOfBoundsException outOfBoundsException) {
            Log.d( "InvalidSkyDiverString", skydiverDeserialisedString );
            return null;
        }
        float alt, seaLevel, mass;
        double lng, lat;
        try {
            alt = Float.parseFloat( massValue );
            lat = Double.parseDouble( latValue );
            lng = Double.parseDouble( lngValue );
            mass = Float.parseFloat( massValue );
            seaLevel = Float.parseFloat( seaLevelValue );
        }
        catch (NumberFormatException nfe) {
            Log.d( "InvalidSkyDiverStringValues", skydiverDeserialisedString );
            return null;
        }
        
        UserPreferences up = new UserPreferences();
        up.mass = mass;
        up.name = name;
        up.seaLevel = seaLevel;
        
        UserInformation ui = 
                UserInformation.getUserInformationFromUserPreferences( up );
        
        SkyDiver sd = new SkyDiver(ui);
        
        MetersSensorValue altitude = new MetersSensorValue();
        altitude.setRawValue( alt );
        LatitudeSensorValue latitude = new LatitudeSensorValue();
        latitude.setRawValue( lat );
        LongitudeSensorValue longitude = new LongitudeSensorValue();
        longitude.setRawValue( lng );
        Position position = new Position(longitude, latitude, altitude);
        sd.updatePositionInformation( position );
        
        return sd;
    }
    
    @Override
    public synchronized void updatePositionInformation( Position newPosition ) {

        if ( lastKnownPosition == null ) {
            lastKnownPosition =
                    new Position( newPosition.getLng(), newPosition.getLat(),
                            newPosition.getAlt() );
        } else {
            lastKnownPosition.setAlt( newPosition.getAlt() );
            lastKnownPosition.setLat( newPosition.getLat() );
            lastKnownPosition.setLng( newPosition.getLng() );
        }

    }

    @Override
    public String toString() {
        String lat="null";
        String lng = "null";
        String alt = "null";
        if (lastKnownPosition != null) {
            lat = lastKnownPosition.getLat() == null ? "null" : lastKnownPosition.getLat().toString();
            lng = lastKnownPosition.getLat() == null ? "null" : lastKnownPosition.getLng().toString();
            alt = lastKnownPosition.getAlt() == null ? "null" : lastKnownPosition.getAlt().toString();
        }
        
        return String.format( "%s:%.2f|%.2f|%s|%s|%s", userInformation.getName(),
                userInformation.getMass(),
                userInformation.getSeaLevelCalibration(),
                lat, lng, alt);
    }

    public Position getPosition() {
        return lastKnownPosition;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ( ( userInformation == null ) ? 0 : userInformation
                                .hashCode() );
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( ! ( obj instanceof SkyDiver ) ) {
            return false;
        }
        SkyDiver other = (SkyDiver) obj;
        if ( userInformation == null ) {
            if ( other.userInformation != null ) {
                return false;
            }
        } else if ( !userInformation.equals( other.userInformation ) ) {
            return false;
        }
        return true;
    }
    
    

}
