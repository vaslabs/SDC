package com.vaslabs.logbook;

import android.test.AndroidTestCase;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;

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

    LogbookAPI logbookAPI = Mockito.mock(LogbookAPI.class);

    public void setUp() {
    }

    public void test_logbook_summary_object_mock_data() throws Exception {
        CommunicationManager cm = Mockito.mock(CommunicationManager.class);
        Constructor<Response> summaryLogbookResponseConstructor = Response.class.getDeclaredConstructor(JSONArray.class, Integer.TYPE);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject summaryJS = new JSONObject();
        summaryJS.accumulate("numberOfJumbs", 3);
        Date d = Calendar.getInstance().getTime();
        summaryJS.accumulate("latestJumbDate", d);
        summaryJS.accumulate("averageExitAltitude", 3400);
        summaryJS.accumulate("averageDeployAltitude", 1000);
        summaryJS.accumulate("averageSpeed", 100.1);
        summaryJS.accumulate("averageTopSpeed", 200.0);
        jsonObject.accumulate("logbookSummary", summaryJS);
        jsonArray.put(jsonObject);
        Response summaryLogbookResponse = summaryLogbookResponseConstructor.newInstance(jsonArray, 0);
        Mockito.when(cm.sendRequest("{\"action\":0}", Mockito.anyString())).thenReturn(summaryLogbookResponse);
        LogbookSummary logbookSummary = LogbookAPI.INSTANCE.getLogbookSummary(cm, this.mContext);
        assertEquals(3, logbookSummary.getNumberOfJumbs());
        assertEquals(d, logbookSummary.getLatestJumbDate());
        assertEquals(3400, logbookSummary.getAverageExitAltitude());
        assertEquals(1000, logbookSummary.getAverageDeployAltitude());
        assertEquals(100.1, logbookSummary.getAverageSpeed());
        assertEquals(200.0, logbookSummary.getAverageTopSpeed());
    }

}
