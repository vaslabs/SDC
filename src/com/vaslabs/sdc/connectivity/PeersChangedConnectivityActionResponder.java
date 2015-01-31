package com.vaslabs.sdc.connectivity;

import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

/**
 * Manages changes on the discoverable list of peers.
 * WifiP2pManager.requestPeers() gets the list of current peers
 * @author Vasilis Nicolaou
 *
 */
public class PeersChangedConnectivityActionResponder extends
        ConnectivityActionResponder<ListView> {

    @Override
    public void manageAction( Context context, Intent intent ) {
        
    }

    @Override
    public ListView getView() {
        // TODO Auto-generated method stub
        return null;
    }

}
