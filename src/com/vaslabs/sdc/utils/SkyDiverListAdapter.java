package com.vaslabs.sdc.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SkyDiverListAdapter extends BaseAdapter implements SkyDiverEnvironmentUpdate {

    private Map<String, SkyDiver> knownSkyDivers;
    private List<SkyDiver> knownSkyDiversList;
    
    public SkyDiverListAdapter() {
        knownSkyDivers = new HashMap<String, SkyDiver>();    
    }
    
    
    @Override
    public int getCount() {
        return knownSkyDivers.size();
    }

    @Override
    public Object getItem( int position ) {
        return null;
    }

    @Override
    public long getItemId( int position ) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public synchronized void onNewSkydiverInfo( SkyDiver skydiver ) {
        if (knownSkyDivers.containsKey( skydiver.getName() )) {
            onSkydiverInfoUpdate( skydiver );
        } else {
            knownSkyDivers.put( skydiver.getName(), skydiver );
        }
        
    }

    @Override
    public synchronized void onSkydiverInfoUpdate( SkyDiver skydiver ) {
        if (!this.knownSkyDivers.containsKey( skydiver )) {
            onNewSkydiverInfo( skydiver );
        } else {
            SkyDiver previouslyKnownSkyDiver = this.knownSkyDivers.get( skydiver.getName());
            if (skydiver.getConnectivityStrengthAsInt() != previouslyKnownSkyDiver.getConnectivityStrengthAsInt()) {
                onConnectivityChange( skydiver );
            } else {
                previouslyKnownSkyDiver.updatePositionInformation( skydiver.getPosition() );
                Collections.sort( this.knownSkyDiversList );
                //also speed && direction which are not yet available TODO
            }
        }
    }



    @Override
    public synchronized void onConnectivityChange( SkyDiver skydiver ) {
        
        if (skydiver.getConnectivityStrengthAsInt() == SDConnectivity.CONNECTION_LOST.ordinal()) {
            onLooseConnection(skydiver);
        } else {
            SkyDiver sd = knownSkyDivers.get( skydiver.getName() );
            if (sd != null) {
                sd.setConnectivityStrength( 
                        SDConnectivity.values()[skydiver.getConnectivityStrengthAsInt()] );
            }
        }
        
    }



    @Override
    public synchronized void onLooseConnection( SkyDiver skydiver ) {
        SkyDiver sd = knownSkyDivers.get( skydiver.getName() );
        if (sd != null) {
            sd.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
            //possibly do a warning implementation here.
        }
    }

}
