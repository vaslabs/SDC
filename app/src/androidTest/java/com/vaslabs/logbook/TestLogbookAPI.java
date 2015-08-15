package com.vaslabs.logbook;

import android.test.AndroidTestCase;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vnicolaou on 15/08/15.
 */
public class TestLogbookAPI extends AndroidTestCase{

    LogbookAPI logbookAPI = null;

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().toString());
        logbookAPI = LogbookAPI.INSTANCE;
    }

    public void test_logbook_summary_object_mock_data() throws Exception {
        CommunicationManager cm = Mockito.mock(CommunicationManager.class);
        Constructor<Response> summaryLogbookResponseConstructor = Response.class.getDeclaredConstructor(JSONArray.class, Integer.TYPE);
        summaryLogbookResponseConstructor.setAccessible(true);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject summaryJS = new JSONObject();
        summaryJS.accumulate("numberOfJumbs", 3);
        long dMillis = Calendar.getInstance().getTimeInMillis();
        summaryJS.accumulate("latestJumbDate", dMillis);
        summaryJS.accumulate("averageExitAltitude", 3400);
        summaryJS.accumulate("averageDeployAltitude", 1000);
        summaryJS.accumulate("averageSpeed", 100.1);
        summaryJS.accumulate("averageTopSpeed", 200.0);
        jsonObject.accumulate("logbookSummary", summaryJS);
        jsonArray.put(summaryJS);
        Response summaryLogbookResponse = summaryLogbookResponseConstructor.newInstance(jsonArray, 0);
        Mockito.when(cm.sendRequest("{\"action\":0}", API.getApiToken(this.mContext))).thenReturn(summaryLogbookResponse);
        LogbookSummary logbookSummary = logbookAPI.getLogbookSummary(cm, this.mContext);
        assertEquals(3, logbookSummary.getNumberOfJumbs());
        assertEquals(dMillis, logbookSummary.getLatestJumbDate());
        assertEquals(3400, logbookSummary.getAverageExitAltitude());
        assertEquals(1000, logbookSummary.getAverageDeployAltitude());
        assertEquals(100.1, logbookSummary.getAverageSpeed());
        assertEquals(200.0, logbookSummary.getAverageTopSpeed());
    }

}
