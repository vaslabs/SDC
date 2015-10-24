package com.vaslabs.sdc.entries;

/**
 * Created by vnicolaou on 24/10/15.
 */
public class SessionEntry {
    private BarometerEntries barometerEntries;
    private GpsEntries gpsEntries;
    private ConnectionEntries connectionEntries;

    public BarometerEntries getBarometerEntries() {
        return barometerEntries;
    }

    public GpsEntries getGpsEntries() {
        return gpsEntries;
    }

    public ConnectionEntries getConnectionEntries() {
        return connectionEntries;
    }
}
