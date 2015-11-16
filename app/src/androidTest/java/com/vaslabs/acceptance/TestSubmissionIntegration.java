package com.vaslabs.acceptance;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;
import com.vaslabs.structs.DateStruct;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by vnicolao on 12/07/15.
 */
public class TestSubmissionIntegration extends AndroidTestCase{

    @Override
    public void setUp() throws Exception {
        try {
            String apiKey = API.getApiToken(this.getContext());
        } catch (Exception e) {
            try {
                API.saveApiToken(this.getContext(), this.getContext().getString(R.string.test_token));
            } catch (IOException e1) {
                fail(e1.toString());
            }
        }
        super.setUp();
    }

    public void test_that_data_can_be_submitted() throws Exception {

        SDCLogManager logManager = SDCLogManager.getInstance(mContext);
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.sample_log));
        String jsonString = LogUtils.parse(isr);
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);

        float min = SDCMathUtils.findMin(sessionData.getBarometerEntries());
        assertEquals(80.0, min, 5);
        Method numberOfSessionsMethod = SessionFilter.class.getDeclaredMethod("detectNumberOfSessions", BarometerEntries.class, Float.TYPE);
        numberOfSessionsMethod.setAccessible(true);
        int result = (int) numberOfSessionsMethod.invoke(null, sessionData.getBarometerEntries(), min);
        assertEquals(1, result);
        Method timestampsMethod = SessionFilter.class.getDeclaredMethod("detectSessionsTimestamps", BarometerEntries.class, int.class, float.class);
        timestampsMethod.setAccessible(true);
        List<Long> timestamps = (List<Long>) timestampsMethod.invoke(null, sessionData.getBarometerEntries(), result, min);
        assertEquals(1, timestamps.size());
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);

        for (DateStruct dates : sessionDates.keySet()) {
            assertEquals(0, dates.sessionRef);
        }

        Map<DateStruct, SkydivingSessionData> successfullySubmittedSessionDates = logManager.submitLogs(sessionDates);
        sessionData = SessionFilter.mostRecent(successfullySubmittedSessionDates);

        try {
            logManager.saveLatestSession(sessionData);
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    public void test_multiple_sessions_of_single_day() throws Exception {
        SDCLogManager logManager = SDCLogManager.getInstance(mContext);
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.test_multiple_sessions));
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(isr, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        assertEquals(2, sessionDates.size());
        Map<DateStruct, SkydivingSessionData> successfullySubmittedSessionDates = logManager.submitLogs(sessionDates);
        sessionData = SessionFilter.mostRecent(successfullySubmittedSessionDates);
        try {
            logManager.saveLatestSession(sessionData);
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

}
