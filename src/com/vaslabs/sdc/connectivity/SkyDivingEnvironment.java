package com.vaslabs.sdc.connectivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.ui.OnSpeechSuccessListener;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.ui.util.SkyDiverListAdapterHelper;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverEnvironmentUpdate;
import com.vaslabs.sdc.utils.SkyDiverPersonalUpdates;
import com.vaslabs.sdc.utils.SkyDiverPositionalComparator;

public class SkyDivingEnvironment extends BaseAdapter implements SkyDivingInformationListener,
        OnSpeechSuccessListener, SkyDiverEnvironmentUpdate,
        SkyDiverPersonalUpdates {
    private Map<String, SkyDiver> skydivers;
    private List<SkyDiver> skydiversList;
    private SkyDiver myself;
    private Context context;
    private static SkyDivingEnvironment environmentInstance = null;
    private SpeechCommunicationManager scm;
    private final int[] colors = SkyDiverListAdapterHelper.getColors();
    private final int defaultColor = SkyDiverListAdapterHelper.getDefaultColor();
    
    private SkyDivingEnvironment(Context context) {
        skydivers = new HashMap<String, SkyDiver>();
        this.context = context;
        UserInformation ui = UserInformation.getUserInfo( context );
        myself = new SkyDiver( ui );
        scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( context, this );
        skydiversList = new ArrayList<SkyDiver>();
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
            if (skydivers.containsKey( skydiver.getName() )) {
                skydiver.updatePositionInformation( skydiver.getPosition() );
            } else {
                skydivers.put(skydiver.getName(), skydiver );
                skydiversList.add(skydiver);
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
    
    @Override
    public synchronized void onNewSkydiverInfo( SkyDiver skydiver ) {
        if (skydivers.containsKey( skydiver.getName() )) {
            onSkydiverInfoUpdate( skydiver );
        } else {
            skydivers.put( skydiver.getName(), skydiver );
            skydiversList.add( skydiver );
            Collections.sort( this.skydiversList, new SkyDiverPositionalComparator( myself )  );
            SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
            scm.getProximityWarning( context );
        }
        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onSkydiverInfoUpdate( SkyDiver skydiver ) {
        if (!this.skydivers.containsKey( skydiver.getName() )) {
            onNewSkydiverInfo( skydiver );
        } else {
            SkyDiver previouslyKnownSkyDiver = this.skydivers.get( skydiver.getName());
            if (skydiver.getConnectivityStrengthAsInt() != previouslyKnownSkyDiver.getConnectivityStrengthAsInt()) {
                onConnectivityChange( skydiver );
            } else {
                previouslyKnownSkyDiver.updatePositionInformation( skydiver.getPosition() );
                Collections.sort( this.skydiversList, new SkyDiverPositionalComparator( myself )  );
                //also speed && direction which are not yet available TODO
            }
        }
        this.notifyDataSetChanged();
    }



    @Override
    public synchronized void onConnectivityChange( SkyDiver skydiver ) {
        
        if (skydiver.getConnectivityStrengthAsInt() == SDConnectivity.CONNECTION_LOST.ordinal()) {
            onLooseConnection(skydiver);
        } else {
            SkyDiver sd = skydivers.get( skydiver.getName() );
            if (sd != null) {
                sd.setConnectivityStrength( 
                        SDConnectivity.values()[skydiver.getConnectivityStrengthAsInt()] );
            }
        }
        
        Collections.sort( this.skydiversList, new SkyDiverPositionalComparator( myself )  );

        this.notifyDataSetChanged();
    }



    @Override
    public synchronized void onLooseConnection( SkyDiver skydiver ) {
        SkyDiver sd = skydivers.get( skydiver.getName() );
        if (sd != null) {
            SpeechCommunicationManager.getInstance().informAboutdisconnection(
                    SDConnectivity.values()[sd.getConnectivityStrengthAsInt()], context);
            sd.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
        }

        this.notifyDataSetChanged();
    }


    @Override
    public synchronized void onMyAltitudeUpdate( MetersSensorValue hpa ) {
        myself.getPosition().setAlt( hpa );
    }


    @Override
    public synchronized void onMyGPSUpdate( LatitudeSensorValue lat, LongitudeSensorValue lng ) {
        myself.getPosition().setLat( lat );
        myself.getPosition().setLng( lng );
        
    }

    public int getOtherSkyDiversSize() {
        return this.skydivers.size();
    }

    public SkyDiver getSkyDiver( int position ) {
        return skydiversList.get( position );
    }

    @Override
    public int getCount() {
        return skydiversList.size();
    }

    @Override
    public SkyDiver getItem( int position ) {
        return skydiversList.get( position );
    }

    @Override
    public long getItemId( int position ) {
        return 0;
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
}
