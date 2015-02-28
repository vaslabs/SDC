package com.vaslabs.sdc.connectivity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;

public class ActionResponderFactory {

    private static PeersChangedConnectivityActionResponder peersChangedAction = new
            PeersChangedConnectivityActionResponder();
    
    public static ConnectivityActionResponder getResponder( String action, Context c ) {
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals( action )) {
            return new StateChangedConnectivityActionResponder();
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            return peersChangedAction;
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            return peersChangedAction;
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            return new WifiStateChangedConnectivityActionResponder();
        }
        return null;
    }

}
