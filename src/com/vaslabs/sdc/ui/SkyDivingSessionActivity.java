package com.vaslabs.sdc.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.connectivity.WirelessBroadcastReceiver;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
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
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WirelessBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechCommunicationManager.getInstance().shutdown();        
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
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
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        
        mManager = (WifiP2pManager) getSystemService(this.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WirelessBroadcastReceiver(mManager, mChannel, this);
        //initialise environment
        SkyDivingEnvironment.getInstance( this );
        mManager.discoverPeers( mChannel, new WifiActionListener( mManager, mChannel ) );
        
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }    
    
}

class WifiActionListener implements WifiP2pManager.ActionListener {

    private WifiP2pManager manager;
    private Channel channel;
    public WifiActionListener(WifiP2pManager manager, Channel channel) {
        this.manager = manager;
        this.channel = channel;
    }
    
    @Override
    public void onFailure( int reason ) {
    }

    @Override
    public void onSuccess() {
    }
    
}
