package com.vaslabs.sdc.connectivity;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.ui.OnSpeechSuccessListener;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.utils.SkyDiver;

public class SkyDivingEnvironment implements SkyDivingInformationListener, OnSpeechSuccessListener {
    private Set<SkyDiver> skydivers;
    private SkyDiver myself;
    private Context context;
    private static SkyDivingEnvironment environmentInstance = null;
    private SpeechCommunicationManager scm;
    private SkyDivingEnvironment(Context context) {
        skydivers = new HashSet<SkyDiver>();
        this.context = context;
        UserInformation ui = UserInformation.getUserInfo( context );
        myself = new SkyDiver( ui );
        scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( context, this );
    }
    
    public synchronized static SkyDivingEnvironment getInstance(Context c) {
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
                scm.getProximityWarning( context );
            }
        }
    }

    @Override
    public void onSuccess() {
        scm.getTalkingAvailable(context);
        
    }

    @Override
    public void onFailure() {
        // TODO warning
        
    }
}
