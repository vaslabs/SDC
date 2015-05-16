package com.vaslabs.sdc.connectivity;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;

import com.vaslabs.sdc.utils.SkyDiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages changes on the discoverable list of peers.
 * WifiP2pManager.requestPeers() gets the list of current peers
 * @author Vasilis Nicolaou
 *
 */
public class PeersChangedConnectivityActionResponder extends
        ConnectivityActionResponder {

        private volatile boolean knownState = false;
        private Map<String, Boolean> previouslyDiscoveredPresence;
        public PeersChangedConnectivityActionResponder() {
                previouslyDiscoveredPresence = new HashMap<String, Boolean>();
        }

        public boolean stateIsKnown() {
                return knownState;
        }

        public void setStateKnown() {
                knownState = true;
        }

        public void setStateUnknown() {
                knownState = false;
        }

        private synchronized void setAbsent(String skyDiver) {
                if (previouslyDiscoveredPresence.containsKey( skyDiver )) {
                        previouslyDiscoveredPresence.put( skyDiver, false );
                }
        }

        @Override
        public synchronized void onPeersAvailable( WifiP2pDeviceList peers ) {

                Log.i("onPeersAvailable", "" + peers.getDeviceList().size());
                SkyDivingEnvironment environment = SkyDivingEnvironment.getInstance( );
                if (environment == null)
                        return;

                for (String sdKey : previouslyDiscoveredPresence.keySet()) {
                        setAbsent(sdKey);
                }
                List<SkyDiver> skydivers = getPeersAsSkyDivers(peers);
                for (SkyDiver skydiver : skydivers) {
                        environment.onNewSkydiverInfo(skydiver);
                        setPresent(skydiver.getName());


                }

                manageDisconnections();

        }

        private synchronized void setPresent(String skyDiver) {
                previouslyDiscoveredPresence.put( skyDiver, true );
        }

        private synchronized void manageDisconnections() {
                for (String key : previouslyDiscoveredPresence.keySet()) {
                        if (!previouslyDiscoveredPresence.get(key)) {
                                SkyDivingEnvironment.getInstance().onLooseConnection(key);
                                previouslyDiscoveredPresence.remove(key);
                        }

                }
        }

        private List<SkyDiver> getPeersAsSkyDivers(WifiP2pDeviceList peers) {
                Collection<WifiP2pDevice> devices = peers.getDeviceList();
                List<SkyDiver> skydivers = new ArrayList<SkyDiver>();
                for (WifiP2pDevice device : devices) {
                        skydivers.add( SkyDiver.valueOf(device) );

                }
                return skydivers;
        }
}
