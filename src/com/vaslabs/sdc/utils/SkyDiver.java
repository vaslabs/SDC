package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.UserInformation;

public class SkyDiver implements PositionalUpdate {
    private UserInformation userInformation;
    private Position lastKnownPosition;

    public SkyDiver( UserInformation ui ) {
        userInformation = UserInformation.getUserInformationClone( ui );
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

}
