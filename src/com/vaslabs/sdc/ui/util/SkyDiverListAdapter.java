package com.vaslabs.sdc.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverEnvironmentUpdate;
import com.vaslabs.sdc.utils.SkyDiverPersonalUpdates;
import com.vaslabs.sdc.utils.SkyDiverPositionalComparator;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SkyDiverListAdapter extends BaseAdapter implements SkyDiverEnvironmentUpdate, SkyDiverPersonalUpdates {

    private Map<String, SkyDiver> knownSkyDivers;
    private List<SkyDiver> knownSkyDiversList;
    private SkyDiver me;
    private int[] colors;
    private int defaultColor = Color.CYAN;
    private Context context;
    public SkyDiverListAdapter(SkyDiver me, Context c) {
        knownSkyDivers = new HashMap<String, SkyDiver>();
        knownSkyDiversList = new ArrayList<SkyDiver>();
        this.me = me;
        colors = new int[SDConnectivity.values().length];
        colors[SDConnectivity.CONNECTION_LOST.ordinal()] = Color.GRAY;
        colors[SDConnectivity.WEAK.ordinal()] = Color.YELLOW;
        colors[SDConnectivity.MEDIUM.ordinal()] = Color.MAGENTA;
        colors[SDConnectivity.STRONG.ordinal()] = Color.RED;
        context = c;
    }
    
    
    @Override
    public int getCount() {
        return knownSkyDivers.size();
    }

    @Override
    public SkyDiver getItem( int position ) {
        return knownSkyDiversList.get( position );
    }

    @Override
    public long getItemId( int position ) {
        // TODO Auto-generated method stub
        return knownSkyDiversList.get( position ).hashCode();
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        TextView tv = new TextView(parent.getContext());
        int connectivity = getItem(position).getConnectivityStrengthAsInt();
        int color = connectivity < colors.length ? colors[connectivity] : defaultColor;
        tv.setBackgroundColor( color );
        tv.setText( getItem(position).getName() );
        
        return tv;
    }



    @Override
    public synchronized void onNewSkydiverInfo( SkyDiver skydiver ) {
        if (knownSkyDivers.containsKey( skydiver.getName() )) {
            onSkydiverInfoUpdate( skydiver );
        } else {
            knownSkyDivers.put( skydiver.getName(), skydiver );
            knownSkyDiversList.add( skydiver );
            Collections.sort( this.knownSkyDiversList, new SkyDiverPositionalComparator( me )  );
            SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
            scm.getProximityWarning( context );
        }
        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onSkydiverInfoUpdate( SkyDiver skydiver ) {
        if (!this.knownSkyDivers.containsKey( skydiver.getName() )) {
            onNewSkydiverInfo( skydiver );
        } else {
            SkyDiver previouslyKnownSkyDiver = this.knownSkyDivers.get( skydiver.getName());
            if (skydiver.getConnectivityStrengthAsInt() != previouslyKnownSkyDiver.getConnectivityStrengthAsInt()) {
                onConnectivityChange( skydiver );
            } else {
                previouslyKnownSkyDiver.updatePositionInformation( skydiver.getPosition() );
                Collections.sort( this.knownSkyDiversList, new SkyDiverPositionalComparator( me )  );
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
        
        Collections.sort( this.knownSkyDiversList, new SkyDiverPositionalComparator( me )  );
        
    }



    @Override
    public synchronized void onLooseConnection( SkyDiver skydiver ) {
        SkyDiver sd = knownSkyDivers.get( skydiver.getName() );
        if (sd != null) {
            SpeechCommunicationManager.getInstance().informAboutdisconnection(
                    SDConnectivity.values()[sd.getConnectivityStrengthAsInt()], context);
            sd.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
        }
    }


    @Override
    public synchronized void onAltitudeUpdate( MetersSensorValue hpa ) {
        me.getPosition().setAlt( hpa );
    }


    @Override
    public synchronized void onGPSUpdate( LatitudeSensorValue lat, LongitudeSensorValue lng ) {
        me.getPosition().setLat( lat );
        me.getPosition().setLng( lng );
        
    }

}
