package com.vaslabs.sdc.utils;

import java.util.Comparator;

public class SkyDiverPositionalComparator implements Comparator<SkyDiver> {

    private SkyDiver me;
    public SkyDiverPositionalComparator(SkyDiver me) {
        this.me = me;
    }

    @Override
    public int compare( SkyDiver skyDiverA, SkyDiver skyDiverB ) {
        
        if (skyDiverA.getPosition() != null && skyDiverB.getPosition() != null) {
            double distanceFromA = GeoUtils.calculateDistance( me.getPosition(), skyDiverA.getPosition() );
            
            double distanceFromB = GeoUtils.calculateDistance( me.getPosition(), skyDiverB.getPosition() );
            
            distanceFromA = Math.abs( distanceFromA );
            distanceFromB = Math.abs( distanceFromB );
            return distanceFromA - distanceFromB <= 0 ? -1 : 1;
        }
        
        return - skyDiverA.getConnectivityStrengthAsInt() + skyDiverB.getConnectivityStrengthAsInt();
    }

}
