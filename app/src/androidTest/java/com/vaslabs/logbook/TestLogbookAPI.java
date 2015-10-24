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
import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public class TestLogbookAPI extends AndroidTestCase{

    LogbookAPI logbookAPI = null;

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().toString());
        logbookAPI = LogbookAPI.INSTANCE;
        CommunicationManager.getInstance(this.mContext);
    }

    public void test_connectivity() throws Exception {
        CommunicationManager cm = CommunicationManager.getInstance();
        Response response = cm.sendRequest("/logbook/api_get/");
        assertTrue(response.getBody().toString().contains("latitude"));
    }

    public void test_logbook_details_object_mock_data() throws Exception {
        List<Logbook> logbookList = logbookAPI.getLogbookEntries();
        assertEquals(2, logbookList.size());
    }

    public void test_logbook_summary_from_entries() throws Exception {
        List<Logbook> logbookList = logbookAPI.getLogbookEntries();
        LogbookSummary ls = LogbookSummary.fromLogbookEntries(logbookList);
        assertEquals(668.26f, ls.getAverageDeployAltitude());
        assertEquals(-85.18f, ls.getAverageTopSpeed());
        assertEquals(3415.65f, ls.getAverageExitAltitude());
        assertEquals(1434870982000L, ls.getLatestJumpDate());
        assertEquals(2, ls.getNumberOfJumps());
    }


}
