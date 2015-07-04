package com.vaslabs.logs.utils;

import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.entries.ConnectionEntry;
import com.vaslabs.structs.DateStruct;

import java.util.Calendar;
import java.util.HashMap;
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
}
