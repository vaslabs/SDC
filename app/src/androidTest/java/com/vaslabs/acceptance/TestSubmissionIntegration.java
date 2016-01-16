package com.vaslabs.acceptance;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vaslabs.accounts.SdcServiceLocalImpl;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.connectivity.SkydivingSessionListEntry;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;
import com.vaslabs.structs.DateStruct;

import org.json.JSONObject;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vnicolao on 12/07/15.
 */
public class TestSubmissionIntegration extends AndroidTestCase{

    MockSdcLogManager mockedLogManager = new MockSdcLogManager();

    SdcService sdcService;
    CountDownLatch countDownLatch;
    private String previousApiKey;
    private String testToken;
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            countDownLatch.countDown();
        }
    };
    private SkydivingSessionListEntry[] sessionEntries;

    private Response.Listener<String> sessionListListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            sessionEntries = gson.fromJson(response, SkydivingSessionListEntry[].class);
            countDownLatch.countDown();
        }
    };
    private static List<String> lines;

    @Override
    public void setUp() throws Exception {
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().toString());
        try {
            previousApiKey = API.getApiToken(this.getContext());
        } catch (Exception e) {

        }
        testToken = this.getContext().getString(R.string.test_token);
        API.saveApiToken(this.getContext(), testToken);

        prepareMockedData();
        sdcService = new SdcServiceLocalImpl(mContext);

        Response.Listener<JSONObject> submittedListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                countDownLatch.countDown();
            }
        };

        Field field = SDCLogManager.class.getDeclaredField("submittedListener");
        field.setAccessible(true);
        field.set(mockedLogManager, submittedListener);

        field = SDCLogManager.class.getDeclaredField("errorListener");
        field.setAccessible(true);
        field.set(mockedLogManager, errorListener);

        field = SDCLogManager.class.getDeclaredField("logManager");
        field.setAccessible(true);
        field.set(null, mockedLogManager);

        field = SDCLogManager.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(mockedLogManager, mContext);

    }

    @Override
    public void tearDown() throws IOException {
        if (previousApiKey != null)
            API.saveApiToken(this.getContext(), previousApiKey);
    }

    private void prepareMockedData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(R.raw.sample_log)));
        String line;
        lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }



    }

    public void test_that_data_can_be_submitted() throws Exception {

        countDownLatch = new CountDownLatch(1);
        sdcService.getSessionList(testToken, sessionListListener, errorListener);
        countDownLatch.await();
        int currentSize = sessionEntries.length;
        countDownLatch = new CountDownLatch(1);
        mockedLogManager.submitLogs(sdcService);
        countDownLatch.await();
        countDownLatch = new CountDownLatch(1);
        sdcService.getSessionList(testToken, sessionListListener, errorListener);
        countDownLatch.await();
        int expectedSize = currentSize + 1;
        assertEquals(expectedSize, sessionEntries.length);
    }
/*
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
    */

    private static class MockSdcLogManager extends SDCLogManager {

        @Override
        public List<String> loadLogs() {
            return lines;
        }
    }

}
