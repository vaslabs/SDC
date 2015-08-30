package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.structs.DateStruct;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by vnicolao on 04/07/15.
 */
public class TestConvertLogsToJson extends AndroidTestCase {

    private SkydivingSessionData sessionData;
    private String jsonString;
    private Map<DateStruct, SkydivingSessionData> sessionDates;

    private void onSessionEnded() {
        InputStreamReader jsonReader = new InputStreamReader(
                mContext.getResources().openRawResource(R.raw.sample_log));

        try {
            jsonString = LogUtils.parse(jsonReader);

        } catch (IOException e) {
            fail(e.toString());
        }
    }

    private void thenParseLogs() throws JSONException {
        Gson gson = new Gson();
        sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
    }

    private void validateDataFromJson() {

        assertEquals(2033, sessionData.barometerEntriesSize());
        assertEquals(1032, sessionData.gpsEntriesSize());
        assertEquals(2, sessionData.connectionEventsSize());
    }

    private void whenApplyFilters() {
        sessionDates = SessionFilter.filter(sessionData);
    }

    public void test_deserialised_data() throws Exception {
        onSessionEnded();
        thenParseLogs();
        validateDataFromJson();

        whenApplyFilters();
        if (sessionDates.keySet().size() <= 1) {
            fail("Expected more than 1 sessions but found: " + sessionDates.keySet().size());
        }
        thenFindTheMostRecentOne();
        andCheckDateToBeRight();

    }


    private void andCheckDateToBeRight() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sessionData.getBarometerEntry(0).getTimestamp());
        DateStruct ds = new DateStruct(cal);
        assertEquals(2015, ds.year);
        assertEquals(Calendar.JUNE, ds.month);
        assertEquals(28, ds.day);
    }

    private void thenFindTheMostRecentOne() {
        sessionData = SessionFilter.mostRecent(sessionDates);
    }


}
