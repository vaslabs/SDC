package com.vaslabs.sdc.connectivity;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public abstract class ConnectivityActionResponder implements PeerListListener {

    public void manageAction( WifiP2pManager manager, WifiP2pManager.Channel channel ) {
        if (manager != null) {
            manager.requestPeers(channel, this);
        }
    }

}