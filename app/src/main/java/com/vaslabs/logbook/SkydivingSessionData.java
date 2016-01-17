package com.vaslabs.logbook;

import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.ConnectionEntries;
import com.vaslabs.sdc.entries.ConnectionEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GpsEntries;
import com.vaslabs.sdc.entries.GpsEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        BarometerEntries barometerEntries = new BarometerEntries(this.barometerEntries);
        barometerEntries.sort();
        return barometerEntries;
    }

    public GpsEntries getGpsEntries() {
        GpsEntries gpsEntries = new GpsEntries(this.gpsEntries);
        gpsEntries.sort();
        return gpsEntries;
    }

    public ConnectionEntries getConnectionEntries() {
        ConnectionEntries ces = new ConnectionEntries(connectionEntries);
        ces.sort();
        return ces;
    }

    public GpsEntry[] getGpsEntriesAsArray() {
        GpsEntry[] array = new GpsEntry[this.gpsEntries.size()];
        Arrays.sort(array);
        return this.gpsEntries.toArray(array);
    }

    public Entry[] allEntries() {
        Collections.sort(gpsEntries);
        Collections.sort(barometerEntries);
        Entry[] entries = new Entry[gpsEntries.size() + barometerEntries.size()];
        int gpsEntriesIndex = 0;
        int barometerEntriesIndex = 0;
        int generalIndex = 0;
        while (gpsEntriesIndex < gpsEntries.size() && barometerEntriesIndex < barometerEntries.size()) {
            final GpsEntry gpsEntry = gpsEntries.get(gpsEntriesIndex);
            final BarometerEntry barometerEntry = barometerEntries.get(barometerEntriesIndex);
            if (gpsEntry.getTimestamp() < barometerEntry.getTimestamp()) {
                entries[generalIndex++] = gpsEntry;
                gpsEntriesIndex++;
            } else {
                entries[generalIndex++] = barometerEntry;
                barometerEntriesIndex++;
            }
        }

        for (int i = barometerEntriesIndex; i < barometerEntries.size(); i++) {
            entries[generalIndex++] = barometerEntries.get(i);
        }
        for (int i = gpsEntriesIndex; i < gpsEntries.size(); i++) {
            entries[generalIndex++] = gpsEntries.get(i);
        }
        return entries;
    }
}
