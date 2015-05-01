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
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    private int timesBackKeyPressed = 0;
    private long lastTimeKeyPressed = 0;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance();
        if (sde != null)
            sde.writeSensorLogs();
        try {
            unregisterReceiver(mReceiver);
        } catch (RuntimeException re) {
            Log.d("WirelessReceiver, SDC", re.toString());
        } finally {
            try {
                SpeechCommunicationManager.getInstance().shutdown();
            } catch (RuntimeException e) {
                Log.d("Speech, SDC", e.toString());
            } finally {
                if (wakeLock != null && wakeLock.isHeld())
                    wakeLock.release();
            }
        }

    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_sky_diving_session );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        SkyDivingEnvironment.getInstance(this);
        mManager.discoverPeers( mChannel, new WifiActionListener( mManager, mChannel ) );
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "SkyDivingSession");
        wakeLock.acquire();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            timesBackKeyPressed++;
            long now = System.currentTimeMillis();
            if (now - lastTimeKeyPressed < 500 && timesBackKeyPressed >= 3) {
                unregisterReceiver(mReceiver);
                SpeechCommunicationManager.getInstance().shutdown();
                if (wakeLock != null)
                    wakeLock.release();
                finish();
                return true;
            } else {
                lastTimeKeyPressed = now;
            }
        }

        return false;
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
