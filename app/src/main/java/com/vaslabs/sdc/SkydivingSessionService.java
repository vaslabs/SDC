package com.vaslabs.sdc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.connectivity.WirelessBroadcastReceiver;
import com.vaslabs.sdc.ui.Main2Activity;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;

import java.util.logging.Logger;

public class SkydivingSessionService extends Service {

    private SkyDivingEnvironment environment;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WirelessBroadcastReceiver mReceiver;

    public SkydivingSessionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SkydivingSessionService", "created");
        setupNotifications();
    }

    @Override
    public void onDestroy() {
        Log.i("SkydivingSessionService", "Shutting down");
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
            }
        }
        removeNotification();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            environment = SkyDivingEnvironment.getInstance( this );
        } catch (Exception e) {
            Log.e("SkydivingSessionService", e.toString());
        }

        SpeechCommunicationManager scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech(this, environment);
        mManager = (WifiP2pManager) getSystemService(this.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.i("ChannelListener", "Channel disconnected");
            }
        });

        mReceiver = new WirelessBroadcastReceiver(mManager, mChannel, this);

        SkyDivingEnvironment.getInstance().registerWirelessManager(mManager, mChannel, new WifiActionListener(mManager, mChannel));

        Bundle bundle = intent.getExtras();

        return START_STICKY;
    }

    private static final int NOTIFICATION = 1;
    public static final String CLOSE_ACTION = "close";
    @Nullable
    private NotificationManager mNotificationManager = null;
    private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);

    private void setupNotifications() { //called in onCreate()
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, SkydivingSessionService.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Main2Activity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setAction(CLOSE_ACTION),
                0);
        mNotificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_small)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                        getString(R.string.action_exit), pendingCloseIntent)
                .setOngoing(true);
        showNotification();
    }

    private void showNotification() {
        mNotificationBuilder
                .setTicker(getText(R.string.session_started_msg))
                .setContentText(getText(R.string.session_started_msg));
        if (mNotificationManager != null) {
            mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
        }
    }

    private void removeNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager.cancel(NOTIFICATION);
    }
}

class WifiActionListener implements WifiP2pManager.ActionListener {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    public WifiActionListener(WifiP2pManager manager, WifiP2pManager.Channel channel) {
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
