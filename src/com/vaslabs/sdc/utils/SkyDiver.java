package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.UserInformation;

public class SkyDiver implements PositionalUpdate {
    private UserInformation userInformation;
    private Position lastKnownPosition;
    
    public SkyDiver(UserInformation ui) {
        userInformation = UserInformation.getUserInformationClone( ui );
    }

    @Override
    public synchronized void updatePositionInformation( Position newPosition ) {
        
        if (lastKnownPosition == null) {
            lastKnownPosition = new Position(newPosition.getLng(), newPosition.getLat(), newPosition.getAlt());
        }
        else {
            lastKnownPosition.setAlt( newPosition.getAlt() );
            lastKnownPosition.setLat( newPosition.getLat() );
            lastKnownPosition.setLng( newPosition.getLng() );
        }
        
        
    }
    
    
    
}
