package com.vaslabs.sdc.connectivity;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.utils.SkyDiver;

public class SkyDivingEnvironment implements SkyDivingInformationListener {
    private Set<SkyDiver> skydivers;
    private SkyDiver myself;
    private Context context;
    private static SkyDivingEnvironment environmentInstance = null;
    
    private SkyDivingEnvironment(Context context) {
        skydivers = new HashSet<SkyDiver>();
        this.context = context;
        UserInformation ui = UserInformation.getUserInfo( context );
        myself = new SkyDiver( ui );
    }
    
    public synchronized SkyDivingEnvironment getInstance(Context c) {
        if ( environmentInstance == null) {
            environmentInstance = new SkyDivingEnvironment(c);
        }
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
