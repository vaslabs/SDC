package com.vaslabs.sdc.ui;

import java.util.ArrayList;
import java.util.List;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
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
import android.widget.Toast;

public class SkyDivingSessionActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */

    private SkyDivingEnvironment environment;
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WirelessBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private int timesBackKeyPressed = 0;
    private long lastTimeKeyPressed = 0;
    private PowerManager.WakeLock wakeLock;
    private ShimmerTextView shimmerTextView;

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sky_diving_session);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        environment = SkyDivingEnvironment.getInstance( this );

        SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech(this, environment);


        shimmerTextView = (ShimmerTextView) findViewById(R.id.shimmer_session_started_tv);
        shimmerTextView.bringToFront();
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(1000);
        shimmer.setRepeatCount(3);
        shimmer.start(shimmerTextView);
        
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        
        
        mManager = (WifiP2pManager) getSystemService(this.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.i("ChannelListener", "Channel disconnected");
            }
        });

        mReceiver = new WirelessBroadcastReceiver(mManager, mChannel, this);

        SkyDivingEnvironment.getInstance().registerWirelessManager(mManager, mChannel,  new WifiActionListener( mManager, mChannel ) );


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
                SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance();
                if (sde != null)
                    sde.writeSensorLogs();
                try {
                    SpeechCommunicationManager.getInstance().shutdown();
                } catch (Exception e) {Log.e("SPEECH", e.toString());}
                if (wakeLock != null)
                    wakeLock.release();

                finish();
                return true;
            } else {
                lastTimeKeyPressed = now;
                Toast.makeText(this, "Double click back to exit", Toast.LENGTH_LONG).show();
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