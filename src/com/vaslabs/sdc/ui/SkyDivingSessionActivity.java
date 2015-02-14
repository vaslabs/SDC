package com.vaslabs.sdc.ui;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.ui.util.SkyDiverListAdapter;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SkyDivingSessionActivity extends Activity implements EnvironmentUpdate, OnSpeechSuccessListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private ListView connectedSkydiversListView;
    private Button mockAddSkydiverButton;
    private TextView barometerTextView;
    private TextView altimeterTextView;
    private TextView gpsPositionTextView;
    private SkyDiverListAdapter sdListAdapter;
    private static final boolean AUTO_HIDE = true;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechCommunicationManager.getInstance().shutdown();        
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_sky_diving_session );        
        
        
        connectedSkydiversListView = (ListView)findViewById( R.id.skydiversListView );
        mockAddSkydiverButton = (Button)findViewById(R.id.mockAddingSkydiver);
        sdListAdapter = new SkyDiverListAdapter( new SkyDiver(UserInformation.getUserInfo( this )) );
        connectedSkydiversListView.setAdapter( sdListAdapter );
        
        SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( this, this );
        mockAddSkydiverButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                SDConnectivity[] connectivityValues = SDConnectivity.values();
                String id = String.valueOf( (long)(Math.random()*2000000000L));
                int connectivity = (int)(Math.random()*(connectivityValues.length - 1)) + 1;
                SkyDiver sd = SkyDiver.serialiseSkyDiverFromString( id + ":50.00|1014.12|null|null|null" );
                sd.setConnectivityStrength( connectivityValues[connectivity] );
                onNewSkydiverInfo( sd );
                
            }
        } );

    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );

    }


    @Override
    public void onNewSkydiverInfo( SkyDiver skydiver ) {
        sdListAdapter.onNewSkydiverInfo( skydiver );
        SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        scm.getProximityWarning( this );
    }

    @Override
    public void onSkydiverInfoUpdate( SkyDiver skydiver ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onConnectivityChange( SkyDiver skydiver ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLooseConnection( SkyDiver skydiver ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBarometerValueChange( HPASensorValue hpaValue ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGPSUpdate( Position newKnownPosition ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSuccess() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFailure() {
        // TODO Auto-generated method stub
        
    }
    
    
}
