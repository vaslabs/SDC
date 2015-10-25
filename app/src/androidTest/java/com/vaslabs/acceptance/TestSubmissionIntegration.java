package com.vaslabs.acceptance;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.structs.DateStruct;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by vnicolao on 12/07/15.
 */
public class TestSubmissionIntegration extends AndroidTestCase{

    public void test_that_data_can_be_submitted() throws Exception {
        SDCLogManager logManager = SDCLogManager.getInstance(mContext);
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.sample_log));
        String jsonString = LogUtils.parse(isr);
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        sessionData = SessionFilter.mostRecent(sessionDates);

        logManager.submitLogs(sessionDates);
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
        logManager.submitLogs(sessionDates);
        try {
            logManager.saveLatestSession(sessionData);
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

}
