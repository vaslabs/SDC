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
    protected Map<String, Boolean> previouslyDiscoveredPresence;
    private Map<String, SkyDiverConnectionBuffer> connectionBuffer;
    public ConnectivityActionResponder() {
        previouslyDiscoveredPresence = new HashMap<String, Boolean>();
        connectionBuffer = new HashMap<String, SkyDiverConnectionBuffer>();
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

            updateBuffersForConnection(skydiver);
            if (connectionBuffer.get(skydiver.getName()).isLegitEvent()) {
                environment.onNewSkydiverInfo(skydiver);
                setPresent(skydiver.getName());
            }

        }

        manageDisconnections();
        environment.resetWifiManager();
    }

    private void updateBuffersForConnection(SkyDiver skydiver) {
        if (!connectionBuffer.containsKey(skydiver.getName())) {
            connectionBuffer.put(skydiver.getName(), new SkyDiverConnectionBuffer(500));
        }
        connectionBuffer.get(skydiver.getName()).updateConnection();
    }

    private void updateBuffersForDisconnection(String skydiver) {
        if (!connectionBuffer.containsKey(skydiver)) {
            connectionBuffer.put(skydiver, new SkyDiverConnectionBuffer(500));
        }
        connectionBuffer.get(skydiver).updateDisconnection();
    }

    protected synchronized void setPresent(String skyDiver) {
        previouslyDiscoveredPresence.put( skyDiver, true );
    }
    
    protected synchronized void manageDisconnections() {
        for (String key : previouslyDiscoveredPresence.keySet()) {
            updateBuffersForDisconnection(key);
            if (!previouslyDiscoveredPresence.get( key )) {
                if (connectionBuffer.get(key).isLegitEvent()) {
                    SkyDivingEnvironment.getInstance().onLooseConnection(key);
                    previouslyDiscoveredPresence.remove(key);
                }
            }
        }
    }

    protected List<SkyDiver> getPeersAsSkyDivers(WifiP2pDeviceList peers) {
        Collection<WifiP2pDevice> devices = peers.getDeviceList();
        List<SkyDiver> skydivers = new ArrayList<SkyDiver>();
        for (WifiP2pDevice device : devices) {
            skydivers.add( SkyDiver.valueOf(device) );
            
        }
        return skydivers;
    }
    
}

class SkyDiverConnectionBuffer {
    private long lastConnection;
    private long lastDisconnection;
    private final long sensitivity;
    public SkyDiverConnectionBuffer(long sensitivity) {
        this.sensitivity = sensitivity;
    }

    public synchronized void updateConnection() {
        lastConnection = System.currentTimeMillis();
    }

    public synchronized  void updateDisconnection() {
        this.lastDisconnection = System.currentTimeMillis();
    }

    public synchronized boolean isLegitEvent() {
        return Math.abs(lastConnection - lastDisconnection) > sensitivity;
    }

}