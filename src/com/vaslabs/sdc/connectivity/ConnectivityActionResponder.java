package com.vaslabs.sdc.connectivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaslabs.sdc.utils.SkyDiver;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

public abstract class ConnectivityActionResponder implements PeerListListener {
    
    private boolean knownState = false;
    private Map<String, Boolean> previouslyDiscoveredPresence;
    
    public ConnectivityActionResponder() {
        previouslyDiscoveredPresence = new HashMap<String, Boolean>();
    }
    
    public void manageAction( WifiP2pManager manager, Channel channel ) {
        if (manager != null) {
            manager.requestPeers(channel, this);
        }
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
        
    protected synchronized void setAbsent(String skyDiver) {
        if (previouslyDiscoveredPresence.containsKey( skyDiver )) {
            previouslyDiscoveredPresence.put( skyDiver, false );
        }
    }
    
    protected synchronized void setPresent(String skyDiver) {
        previouslyDiscoveredPresence.put( skyDiver, true );
    }
    
    protected synchronized void manageDisconnections() {
        for (String key : previouslyDiscoveredPresence.keySet()) {
            if (!previouslyDiscoveredPresence.get( key )) {
                SkyDivingEnvironment.getInstance().onLooseConnection( key );
                previouslyDiscoveredPresence.remove( key );
            }
        }

    }
    
    @Override
    public synchronized void onPeersAvailable( WifiP2pDeviceList peers ) {
        SkyDivingEnvironment environment = SkyDivingEnvironment.getInstance( );
        if (environment == null)
            return;
        for (String sdKey : previouslyDiscoveredPresence.keySet()) {
            setAbsent(sdKey);
        }
        List<SkyDiver> skydivers = getPeersAsSkyDivers(peers);
        for (SkyDiver skydiver : skydivers) {
            environment.onNewSkydiverInfo( skydiver );
            setPresent(skydiver.getName());
        }
        
        manageDisconnections();
    }

    private List<SkyDiver> getPeersAsSkyDivers( WifiP2pDeviceList peers ) {
        Collection<WifiP2pDevice> devices = peers.getDeviceList();
        List<SkyDiver> skydivers = new ArrayList<SkyDiver>();
        for (WifiP2pDevice device : devices) {
            skydivers.add( SkyDiver.valueOf(device) );
            
        }
        return skydivers;
    }
    
}
