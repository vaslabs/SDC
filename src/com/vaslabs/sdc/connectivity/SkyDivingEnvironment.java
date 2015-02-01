package com.vaslabs.sdc.connectivity;

import java.util.HashSet;
import java.util.Set;

import com.vaslabs.sdc.utils.SkyDiver;

public class SkyDivingEnvironment implements SkyDivingInformationListener {
    private Set<SkyDiver> skydivers;
    private SkyDiver myself;
    private static SkyDivingEnvironment environmentInstance = 
            new SkyDivingEnvironment();
    
    private SkyDivingEnvironment() {
        skydivers = new HashSet<SkyDiver>();
    }
    
    public SkyDivingEnvironment getInstance() {
        return environmentInstance;
    }
    
    @Override
    public void onNewSkydiverInformation( SkyDiver skydiver ) {
        synchronized (skydiver) {
            if (skydivers.contains( skydiver )) {
                skydiver.updatePositionInformation( skydiver.getPosition() );
            } else {
                skydivers.add( skydiver );
            }
        }
    }
}
