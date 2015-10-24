package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionLogbookData;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.R;

import java.io.InputStreamReader;

/**
 * Created by vnicolao on 03/07/15.
 */
public class TestIdentifyingSession extends AndroidTestCase {

    BarometerEntries barometerEntries;
    SkydivingSessionLogbookData logbook;
    public void setUp() {

    }

    private void whenJsonReceived() {
        Gson gson = new Gson();
        InputStreamReader jsonReader = new InputStreamReader(
                this.mContext.getResources().openRawResource(R.raw.barometer_test_data));
        barometerEntries = gson.fromJson(jsonReader, BarometerEntries.class);

        barometerEntries.sort();
    }

    private void andSessionCreated() {
        logbook = SkydivingSessionLogbookData.create(barometerEntries);
    }

    //1737013

    private void validateThatTimesAreRight() {
        BarometerEntry[] barometerEntries = LogbookStats.average(this.barometerEntries, 1000);
        long startedLoggingAt = barometerEntries[0].getTimestamp();
        long filterStartsAt = logbook.getEntryAt(0).getTimestamp();
        long difference = filterStartsAt - startedLoggingAt;
        assertEquals(1733722, difference);
    }

    public void test_filtering_session_data_from_json() {
        whenJsonReceived();
        andSessionCreated();
        validateThatTimesAreRight();
    }


}
