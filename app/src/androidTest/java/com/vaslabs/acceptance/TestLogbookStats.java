package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.types.SkydivingEvent;
import com.vaslabs.sdc.types.SkydivingEventDetails;

/**
 * Created by vnicolaou on 01/11/15.
 */
public class TestLogbookStats extends AndroidTestCase {

    public void test_findBarometerEntry() {
        BarometerEntry[] barometerEntries = new BarometerEntry[]{new BarometerEntry(2, 8f),
        new BarometerEntry(3, 7f), new BarometerEntry(6,8f), new BarometerEntry(8, 10f)};
        assertEquals(0, LogbookStats.findBarometerEntry(barometerEntries, 2));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 6));
        assertEquals(3, LogbookStats.findBarometerEntry(barometerEntries, 7));

        barometerEntries = new BarometerEntry[]{new BarometerEntry(2, 8f),
                new BarometerEntry(3, 7f), new BarometerEntry(6,8f) };
        assertEquals(0, LogbookStats.findBarometerEntry(barometerEntries, 2));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 6));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 7));
    }

    public void test_identifyFlyingEvents() {
        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(this.getContext());
        SkydivingEventDetails[] skydivingEventDetailsArray = LogbookStats.identifyFlyingEvents(skydivingSessionData.getBarometerEntries());
        assertEquals(4, skydivingEventDetailsArray.length);
        assertEquals(skydivingEventDetailsArray[0].eventType, SkydivingEvent.TAKE_OFF);
        assertEquals(skydivingEventDetailsArray[1].eventType, SkydivingEvent.FREE_FALL);
        assertEquals(skydivingEventDetailsArray[2].eventType, SkydivingEvent.CANOPY);
        assertEquals(skydivingEventDetailsArray[3].eventType, SkydivingEvent.LANDING);
        assertTrue(skydivingEventDetailsArray[0].timestamp < skydivingEventDetailsArray[1].timestamp);
        assertTrue(skydivingEventDetailsArray[1].timestamp < skydivingEventDetailsArray[2].timestamp);
        assertTrue(skydivingEventDetailsArray[2].timestamp < skydivingEventDetailsArray[3].timestamp);
    }
}
