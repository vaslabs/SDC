package com.vaslabs.sdc.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
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
public class SkyDivingSessionActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private ListView connectedSkydiversListView;
    private Button mockAddSkydiverButton;
    private TextView barometerTextView;
    private TextView altimeterTextView;
    private TextView gpsPositionTextView;
    private SkyDivingEnvironment environment;
    private Button mockDisconnectSkydiverButton;
    private List<SkyDiver> skyDiversMock;


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
        skyDiversMock = new ArrayList<SkyDiver>();
        
        connectedSkydiversListView = (ListView)findViewById( R.id.skydiversListView );
        mockAddSkydiverButton = (Button)findViewById(R.id.mockAddingSkydiver);
        environment = SkyDivingEnvironment.getInstance( this );
        connectedSkydiversListView.setAdapter( environment );
        
        SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( this, environment );
        mockAddSkydiverButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                SDConnectivity[] connectivityValues = SDConnectivity.values();
                String id = String.valueOf( (long)(Math.random()*2000000000L));
                int connectivity = (int)(Math.random()*(connectivityValues.length - 1)) + 1;
                SkyDiver sd = SkyDiver.serialiseSkyDiverFromString( id + ":50.00|1014.12|null|null|null" );
                sd.setConnectivityStrength( connectivityValues[connectivity] );
                environment.onNewSkydiverInfo( sd );
                skyDiversMock.add( sd );
            }
        } );
        
        mockDisconnectSkydiverButton = (Button) findViewById( R.id.mockDisconnectSkydiver );
        mockDisconnectSkydiverButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                int index = (int)(Math.random()*skyDiversMock.size());
                if (index < skyDiversMock.size()) {
                    SkyDiver sd = skyDiversMock.get( index );
                    SkyDiver newInfoSD = SkyDiver.serialiseSkyDiverFromString( sd.toString() );
                    newInfoSD.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
                    environment.onNewSkydiverInfo( newInfoSD );
                    skyDiversMock.set( index, newInfoSD );
                }
                
            }
        } );
    }


    
    
    
}
