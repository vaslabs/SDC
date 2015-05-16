package com.vaslabs.sdc.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class WirelessBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager wifiP2PManager;
    private Channel mChannel;
    private Context context;
    
    public WirelessBroadcastReceiver(WifiP2pManager wifiManager, Channel channel, Context context) {
        this.wifiP2PManager = wifiManager;
        this.mChannel = channel;
        this.context = context;
    }
    
    @Override
    public void onReceive( Context context, Intent intent ) {
        String action = intent.getAction();
        ConnectivityActionResponder actionResponder = 
                ActionResponderFactory.getResponder(action, this.context);
        actionResponder.manageAction(wifiP2PManager, mChannel);

    }

}
