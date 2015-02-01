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
