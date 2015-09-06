package com.vaslabs.logs.utils;

import android.content.Context;

import com.vaslabs.sdc.logs.SDCLogManager;

import java.util.*;
import java.io.*;
enum SDEntry {CONNECTION, BAROMETER, GPS};
public class LogUtils {

    public static final String END_OF_CONNECTIONS = "=END OF CONNECTIONS=";
    public static final String END_OF_BAROMETER = "=END OF BAROMETER=";
    public static final String END_OF_GPS = "=END OF GPS=";


    public static String parse(InputStreamReader reader) throws IOException {
        SkydivingData sd = readFromFile(new BufferedReader(reader));

        return sd.toString();
    }

    public static String parse(List<String> lines) throws IOException {
        SkydivingData sd = new SkydivingData();
        SDEntry readingEntry = SDEntry.CONNECTION;

        for (String line : lines) {
            if (END_OF_CONNECTIONS.equals(line)) {
                readingEntry = SDEntry.BAROMETER;
                continue;
            } else if (END_OF_BAROMETER.equals(line)) {
                readingEntry = SDEntry.GPS;
                continue;
            } else if (END_OF_GPS.equals(line)) {
                break;
            }

            sd.addEntry(line, readingEntry);

        }
        return sd.toString();
    }


    public static SkydivingData readFromFile(BufferedReader reader) throws IOException {
        IOException error = null;
        SkydivingData sd = new SkydivingData();
        try {
            String line;
            SDEntry readingEntry = SDEntry.CONNECTION;
            while ( (line = reader.readLine()) != null) {
                if (END_OF_CONNECTIONS.equals(line)) {
                    readingEntry = SDEntry.BAROMETER;
                    continue;
                } else if (END_OF_BAROMETER.equals(line)) {
                    readingEntry = SDEntry.GPS;
                    continue;
                } else if (END_OF_GPS.equals(line)) {
                    break;
                }

                sd.addEntry(line, readingEntry);
            }
        } catch (IOException ioE) {
            error = ioE;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    error = e;
                }
            }
        }

        if (error == null)
            return sd;
        throw error;
    }

    public static String buildSessionData(Context c) throws IOException {
        SDCLogManager logManager = SDCLogManager.getInstance(c);
        List<String> logLines = logManager.loadLogs();
        return LogUtils.parse(logLines);
    }
}

abstract class Entry implements Comparable<Entry> {
    public final long timestamp;
    public Entry(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Entry other) {
        long result = this.timestamp - other.timestamp;
        if (result == 0)
            return 0;
        if (result < 0)
            return -1;
        return 1;
    }
}

class ConnectionEntry extends Entry {

    public final String deviceName;
    public final int connectionEvent;
    public static final int NEW_CONNECTION = 1;
    public static final int LOST_CONNECTION = -1;
    public static final int SCANNING_STARTED = 0;
    public static final int LANDED_EVENT = 2;
    public static final String NEW_CONNECTION_INDICATOR = "New connection";
    public static final String LOST_CONNECTION_INDICATOR = "Lost connection";
    private ConnectionEntry(long timestamp, String deviceName, int connectionEvent) {
        super(timestamp);
        this.deviceName = deviceName;
        this.connectionEvent = connectionEvent;
    }

    public static ConnectionEntry valueOf(String entry) {
        String[] parts = entry.split(":");
        long timestamp = Long.parseLong(parts[0]);

        if (parts.length < 3) {
            if (parts[1].equals("Landed")) {
                return new ConnectionEntry(timestamp, "", LANDED_EVENT);
            }
            else{
                return new ConnectionEntry(timestamp, "", SCANNING_STARTED);
            }
        }

        String deviceName = parts[2].trim();
        int connectionEvent = NEW_CONNECTION_INDICATOR.equals(parts[1].trim()) ? NEW_CONNECTION : LOST_CONNECTION;

        return new ConnectionEntry(timestamp, deviceName, connectionEvent);
    }

    public String toString() {
        return "{\"timestamp\":" + String.valueOf(timestamp) + ", \"deviceName\":" + "\"" + deviceName + "\"" + ", \"connectionEvent\":" + String.valueOf(connectionEvent)
                + "}";
    }
}


class GPSEntry extends Entry {

    public final double latitude;
    public final double longitude;

    private GPSEntry(long timestamp, double latitude, double longitude) {
        super(timestamp);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static GPSEntry valueOf(String entry) {
        String[] parts = entry.split(":");
        long timestamp = Long.parseLong(parts[0]);
        String[] position = parts[1].trim().split(",");
        double latitude = Double.parseDouble(position[0]);
        double longitude = Double.parseDouble(position[1]);
        return new GPSEntry(timestamp, latitude, longitude);
    }

    public String toString() {
        return "{\"timestamp\":" + String.valueOf(timestamp) + ", \"latitude\":" + latitude + ", \"longitude\":" + longitude
                + "}";
    }

}

class BarometerEntry extends Entry {

    public final float altitude;
    public final float deltaAltitude;
    private BarometerEntry(long timestamp, float altitude, float deltaAltitude) {
        super(timestamp);
        this.altitude = deltaAltitude;
        this.deltaAltitude = deltaAltitude;
    }

    public static BarometerEntry valueOf(String entry) {
        String[] parts = entry.split(":");
        long timestamp = Long.parseLong(parts[0]);
        String[] valueParts = parts[1].split(",");
        float altitude = Float.parseFloat(valueParts[0]);
        float deltaAltitude;
        if (valueParts.length == 1)
            deltaAltitude = altitude;
        else
            deltaAltitude = Float.parseFloat(valueParts[1]);
        return new BarometerEntry(timestamp, altitude, deltaAltitude);
    }

    public String toString() {
        return "{\"timestamp\":" + String.valueOf(timestamp) + ", \"altitude\":" + altitude
                + "}";
    }
}

class SkydivingData {
    List<BarometerEntry> barometerEntries;
    List<GPSEntry> gpsEntries;
    List<ConnectionEntry> connectionEntries;

    public SkydivingData() {
        barometerEntries = new ArrayList<BarometerEntry>();
        gpsEntries = new ArrayList<GPSEntry>(128);
        connectionEntries = new ArrayList<ConnectionEntry>();
    }

    public void addEntry(String entryLine, SDEntry entryType) {
        switch (entryType) {
            case CONNECTION:
                addConnectionEntry(entryLine);
                break;
            case BAROMETER:
                addBarometerEntry(entryLine);
                break;
            case GPS:
                addGPSEntry(entryLine);
                break;
        }

    }


    private void addConnectionEntry(String entry) {
        ConnectionEntry be = ConnectionEntry.valueOf(entry);
        connectionEntries.add(be);
    }

    private void addBarometerEntry(String entry) {
        BarometerEntry be = BarometerEntry.valueOf(entry);
        barometerEntries.add(be);
    }

    private void addGPSEntry(String entry) {
        GPSEntry gps = GPSEntry.valueOf(entry);
        gpsEntries.add(gps);
    }

    public String toString() {
        return "{" +
                "\"barometerEntries\":" + barometerEntries.toString() + ", " +
                "\"connectionEntries\":" + connectionEntries.toString() + ", " +
                "\"gpsEntries\":" + gpsEntries.toString() +
                "}";
    }


}
