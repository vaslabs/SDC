package com.vaslabs.logbook;

import android.content.Context;

import com.google.gson.Gson;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.Calendar;

/**
 * Created by vnicolaou on 15/08/15.
 */
public enum LogbookAPI implements ILogbookAPI {
    INSTANCE {
        public static final int MY_LOGBOOK_SUMMARY = 0;

        @Override
        public LogbookSummary getLogbookSummary(CommunicationManager communicationManager, Context mContext) {
            String jsonRequest = "{\"action\":" +  MY_LOGBOOK_SUMMARY + "}";
            Gson gson = new Gson();
            try {
                Response response = communicationManager.sendRequest(jsonRequest, API.getApiToken(mContext));
                String body = response.getBody().toString();
                LogbookSummary ls = gson.fromJson(body, LogbookSummary.class);
                return ls;
            } catch (Exception e) {
                return null;
            }
        }
    },
    MOCK {
        @Override
        public LogbookSummary getLogbookSummary(CommunicationManager communicationManager, Context mContext) {
            try {
                CommunicationManager cm = Mockito.mock(CommunicationManager.class);
                Constructor<Response> summaryLogbookResponseConstructor = Response.class.getDeclaredConstructor(JSONArray.class, Integer.TYPE);
                summaryLogbookResponseConstructor.setAccessible(true);
                JSONArray jsonArray = new JSONArray();
                JSONObject summaryJS = new JSONObject();

                summaryJS.accumulate("numberOfJumbs", 3);
                long dMillis = Calendar.getInstance().getTimeInMillis();
                summaryJS.accumulate("latestJumbDate", dMillis);
                summaryJS.accumulate("averageExitAltitude", 3400);
                summaryJS.accumulate("averageDeployAltitude", 1000);
                summaryJS.accumulate("averageSpeed", 100.1);
                summaryJS.accumulate("averageTopSpeed", 200.0);
                jsonArray.put(summaryJS);
                return INSTANCE.getLogbookSummary(cm, mContext);
            } catch (Exception e) {
                return null;
            }
        }
    };




}
