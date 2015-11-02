package com.vaslabs.logbook;

import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.ConnectionEntries;
import com.vaslabs.sdc.entries.ConnectionEntry;
import com.vaslabs.sdc.entries.GpsEntries;
import com.vaslabs.sdc.entries.GpsEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vnicolao on 04/07/15.
 */
public class SkydivingSessionData {

    private List<BarometerEntry> barometerEntries;
    private List<GpsEntry> gpsEntries;
    private List<ConnectionEntry> connectionEntries;

    public SkydivingSessionData() {
        barometerEntries = new ArrayList<BarometerEntry>();
        gpsEntries = new ArrayList<GpsEntry>();
        connectionEntries = new ArrayList<ConnectionEntry>();
    }

    public SkydivingSessionData(List<ConnectionEntry> connectionEntriesInThisSession, List<BarometerEntry> barometerEntriesInThisSession, List<GpsEntry> gpsEntriesInThisSession) {
        this.connectionEntries = new ArrayList<ConnectionEntry>(connectionEntriesInThisSession);
        this.barometerEntries = new ArrayList<BarometerEntry>(barometerEntriesInThisSession);
        this.gpsEntries = new ArrayList<GpsEntry>(gpsEntriesInThisSession);
    }

    public int barometerEntriesSize() {
        return barometerEntries.size();
    }

    public int gpsEntriesSize() {
        return gpsEntries.size();
    }

    public int connectionEventsSize() {
        return connectionEntries.size();
    }

    public BarometerEntry getBarometerEntry(int i) {
        return barometerEntries.get(i);
    }

    public void insert(BarometerEntry be) {
        barometerEntries.add(be);
    }

    public GpsEntry getGpsEntry(int i) {
        return gpsEntries.get(i);
    }

    public void insert(GpsEntry ge) {
        gpsEntries.add(ge);
    }

    public ConnectionEntry getConnectionEntry(int i) {
        return connectionEntries.get(i);
    }

    public void insert(ConnectionEntry ce) {
        connectionEntries.add(ce);
    }

    public BarometerEntries getBarometerEntries() {
        return new BarometerEntries(barometerEntries);
    }

    public GpsEntries getGpsEntries() {
        return new GpsEntries(gpsEntries);
    }

    public ConnectionEntries getConnectionEntries() {
        return new ConnectionEntries(connectionEntries);
    }

    public GpsEntry[] getGpsEntriesAsArray() {
        GpsEntry[] array = new GpsEntry[this.gpsEntries.size()];
        return this.gpsEntries.toArray(array);
    }
}
