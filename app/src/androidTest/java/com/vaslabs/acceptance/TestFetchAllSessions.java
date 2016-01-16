package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.vaslabs.accounts.SdcServiceLocalImpl;
import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vnicolaou on 16/01/16.
 */
public class TestFetchAllSessions extends AndroidTestCase{
    SdcService sdcService;
    private String testToken;
    CountDownLatch countDownLatch;
    private SkydivingSessionData[] skydivingSessionDatas;
    private Response.Listener<String> sessionFetchListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            skydivingSessionDatas = gson.fromJson(response, SkydivingSessionData[].class);
            countDownLatch.countDown();
        }
    };
    private Response.ErrorListener errorListener;

    @Override
    public void setUp() throws IOException {
        sdcService = new SdcServiceLocalImpl(mContext);

        testToken = this.getContext().getString(R.string.test_token);
        API.saveApiToken(this.getContext(), testToken);
    }

    public void test_fetch_all_sessions() throws InterruptedException {
        countDownLatch = new CountDownLatch(1);
        sdcService.getSessionData(testToken, sessionFetchListener, errorListener);
        countDownLatch.await();
        LogbookStats[] logbookStats = LogbookStats.generateLogbookStats(skydivingSessionDatas);
        assertEquals(skydivingSessionDatas.length - 7, logbookStats.length); //there are currently 7 invalid sessions in my tests
        for (LogbookStats logbookStatsEntry : logbookStats) {
            assertTrue(logbookStatsEntry.getDeploymentAltitude() > 500f);
            assertTrue(logbookStatsEntry.getExitAltitude() > 2000f);
            assertTrue(logbookStatsEntry.getFreeFallTime() > 5f);
            assertTrue(logbookStatsEntry.getMaximumSpeed() < 15);
        }

        LogbookSummary logbookSummary = LogbookSummary.fromLogbookEntries(logbookStats);
        assertEquals(skydivingSessionDatas.length - 7, logbookSummary.getNumberOfJumps());
    }
}
