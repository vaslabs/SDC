package com.vaslabs.logs.utils;

import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.ConnectionEntries;
import com.vaslabs.sdc.entries.GpsEntries;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.entries.ConnectionEntry;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.structs.DateStruct;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vnicolao on 04/07/15.
 */
public class SessionFilter {
    public static Map<DateStruct, SkydivingSessionData> filter(SkydivingSessionData sessionData) {
        Map<DateStruct, SkydivingSessionData> sessionMap = new HashMap<DateStruct, SkydivingSessionData>();
        DateStruct ds = null;
        Calendar cal = Calendar.getInstance();
        BarometerEntry be = null;
        GpsEntry ge = null;
        ConnectionEntry ce = null;
        for (int i = 0; i < sessionData.barometerEntriesSize(); i++) {
            be = sessionData.getBarometerEntry(i);
            cal.setTimeInMillis(be.getTimestamp());
            ds = new DateStruct(cal);
            if (!sessionMap.containsKey(ds)) {
                sessionMap.put(ds, new SkydivingSessionData());
            }
            sessionMap.get(ds).insert(be);
        }
        
        for (int i = 0; i < sessionData.gpsEntriesSize(); i++) {
            ge = sessionData.getGpsEntry(i);
            cal.setTimeInMillis(ge.getTimestamp());
            ds = new DateStruct(cal);
            if (!sessionMap.containsKey(ds)) {
                sessionMap.put(ds, new SkydivingSessionData());
            }
            sessionMap.get(ds).insert(ge);
        }

        for (int i = 0; i < sessionData.connectionEventsSize(); i++) {
            ce = sessionData.getConnectionEntry(i);
            cal.setTimeInMillis(ce.getTimestamp());
            ds = new DateStruct(cal);
            if (!sessionMap.containsKey(ds)) {
                sessionMap.put(ds, new SkydivingSessionData());
            }
            sessionMap.get(ds).insert(ce);
        }
        return sessionMap;
    }

    public static SkydivingSessionData mostRecent(Map<DateStruct, SkydivingSessionData> sessionDates) {
        DateStruct mostRecentDate = null;
        SkydivingSessionData mostRecentSession;
        Set<DateStruct> dates = sessionDates.keySet();
        for (DateStruct ds : dates) {
            if (ds.compareTo(mostRecentDate) > 0) {
                mostRecentDate = ds;
            }
        }

        return sessionDates.get(mostRecentDate);
    }

    public static Map<DateStruct, SkydivingSessionData> filterMultiple(Map<DateStruct, SkydivingSessionData> sessionDates) {
        Map<DateStruct, SkydivingSessionData> singleSessionMap = new HashMap<DateStruct, SkydivingSessionData>();
        for (DateStruct ds : sessionDates.keySet()) {
            SkydivingSessionData[] skydivingSessionDataSplitResult = splitToSingleSessions(sessionDates.get(ds));
            for (int i = 0; i < skydivingSessionDataSplitResult.length; i++) {
                singleSessionMap.put(new DateStruct(ds.year, ds.month, ds.day, i), skydivingSessionDataSplitResult[i]);
            }
        }

        return singleSessionMap;
    }

    private static SkydivingSessionData[] splitToSingleSessions(SkydivingSessionData skydivingSessionData) {
        skydivingSessionData.getBarometerEntries().sort();
        skydivingSessionData.getGpsEntries().sort();
        skydivingSessionData.getConnectionEntries().sort();
        BarometerEntries barometerEntries = skydivingSessionData.getBarometerEntries();
        float findMinimumAltitude = SDCMathUtils.findMin(barometerEntries);
        int numberOfSessionsContained = detectNumberOfSessions(barometerEntries, findMinimumAltitude);
        List<Long> timestamps = detectSessionsTimestamps(barometerEntries, numberOfSessionsContained, findMinimumAltitude);

        SkydivingSessionData[] sessions = rebuildSessions(numberOfSessionsContained, timestamps, skydivingSessionData);
        return sessions;
    }

    private static SkydivingSessionData[] rebuildSessions(int numberOfSessionsContained, List<Long> timestamps, SkydivingSessionData skydivingSessionData) {
        SkydivingSessionData[] singleSessionData = new SkydivingSessionData[numberOfSessionsContained];
        int sessionCounter = 0;
        BarometerEntries barometerEntries;
        GpsEntries gpsEntries;
        ConnectionEntries connectionEntries;
        int barometerEntryIndex = 0;
        int gpsEntryIndex = 0;
        int connectionEntriesIndex = 0;
        long timestampLimit;
        for (int i = 0; i < timestamps.size(); i++) {
            barometerEntries = skydivingSessionData.getBarometerEntries();
            if (i+1 >= timestamps.size()) {
                timestampLimit = SDCMathUtils.findMaxTimestamp(skydivingSessionData);
            } else {
                timestampLimit = timestamps.get(i+1);
            }
            final List<BarometerEntry> barometerEntriesInThisSession = new ArrayList<BarometerEntry>();
            while (barometerEntryIndex < barometerEntries.size() && barometerEntries.get(barometerEntryIndex).getTimestamp() <= timestampLimit) {
                barometerEntriesInThisSession.add(barometerEntries.get(barometerEntryIndex));
                barometerEntryIndex++;
            }

            final List<GpsEntry> gpsEntriesInThisSession = new ArrayList<GpsEntry>();
            gpsEntries = skydivingSessionData.getGpsEntries();
            gpsEntries.sort();
            while (gpsEntryIndex < gpsEntries.size() && gpsEntries.getEntry(gpsEntryIndex).getTimestamp() <= timestampLimit) {
                gpsEntriesInThisSession.add(gpsEntries.getEntry(gpsEntryIndex));
                gpsEntryIndex++;
            }

            final List<ConnectionEntry> connectionEntriesInThisSession = new ArrayList<ConnectionEntry>();
            connectionEntries = skydivingSessionData.getConnectionEntries();
            while (connectionEntriesIndex < connectionEntries.size() && connectionEntries.getEntry(connectionEntriesIndex).getTimestamp() <= timestampLimit) {
                connectionEntriesInThisSession.add(connectionEntries.getEntry(connectionEntriesIndex));
                connectionEntriesIndex++;
            }
            singleSessionData[sessionCounter++] = new SkydivingSessionData(connectionEntriesInThisSession, barometerEntriesInThisSession, gpsEntriesInThisSession);
        }

        return singleSessionData;
    }

    private static List<Long> detectSessionsTimestamps(BarometerEntries barometerEntries, int expectedNumberOfSessions, float baseAltitude) {
        List<Long> timestamps = new ArrayList<Long>();
        BarometerEntry be;
        boolean reset = true;
        for (int i = 0; i < barometerEntries.size(); i++) {
            be = barometerEntries.get(i);
            if (be.getAltitude() > 1000 && reset) {
                reset = false;
            } else if (!reset && be.getAltitude() < baseAltitude + 5) {
                reset = true;
                timestamps.add(be.getTimestamp());
                if (timestamps.size() - 1 == expectedNumberOfSessions)
                    break;
            }
        }
        return timestamps;
    }

    private static int detectNumberOfSessions(BarometerEntries barometerEntries, float baseAltitude) {
        barometerEntries.sort();
        int numberOfSessions = 0;
        BarometerEntry be;
        boolean reset = true;
        for (int i = 0; i < barometerEntries.size(); i++) {
            be = barometerEntries.get(i);
            if (be.getAltitude() > 1000 && reset) {
                reset = false;
            } else if (!reset && be.getAltitude() <= baseAltitude + 5) {
                reset = true;
                numberOfSessions++;
            }
        }
        return numberOfSessions;
    }
}
