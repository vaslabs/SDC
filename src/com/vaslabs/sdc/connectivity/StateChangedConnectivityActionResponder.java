package com.vaslabs.sdc.connectivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.ImageButton;

/**
 *  Checks to see if Wi-Fi is enabled and notifies appropriate activity to manage events where
 *  wifi is or is not enabled
 * @author Vasilis Nicolaou
 *
 */

public class StateChangedConnectivityActionResponder extends
        ConnectivityActionResponder<ImageButton> {

    private int state;
    private final ImageButton stateButton;
    
    public StateChangedConnectivityActionResponder(Context context) {
        stateButton = new ImageButton(context);
    }
    
    @Override
    public void manageAction( Context context, Intent intent ) {
        state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            stateButton.setBackgroundResource( android.R.drawable.button_onoff_indicator_on );
        } else {
            stateButton.setBackgroundResource( android.R.drawable.button_onoff_indicator_off );
        }
        this.setStateKnown();
        
    }


    @Override
    public ImageButton getView() {
        return stateButton;
    }

}
