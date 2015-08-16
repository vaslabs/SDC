package com.vaslabs.logbook;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONArray;
import org.json.JSONObject;

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
                Constructor<Response> summaryLogbookResponseConstructor = Response.class.getDeclaredConstructor(JSONArray.class, Integer.TYPE);
                summaryLogbookResponseConstructor.setAccessible(true);
                JSONArray jsonArray = new JSONArray();
                JSONObject summaryJS = new JSONObject();

                summaryJS.accumulate("numberOfJumps", 3);
                long dMillis = Calendar.getInstance().getTimeInMillis();
                summaryJS.accumulate("latestJumpDate", dMillis);
                summaryJS.accumulate("averageExitAltitude", 3400);
                summaryJS.accumulate("averageDeployAltitude", 1000);
                summaryJS.accumulate("averageSpeed", 20.1);
                summaryJS.accumulate("averageTopSpeed", 85.0);
                jsonArray.put(summaryJS);
                Response summaryLogbookResponse = summaryLogbookResponseConstructor.newInstance(jsonArray, 0);

                String body = summaryLogbookResponse.getBody().toString();
                Gson gson = new Gson();
                LogbookSummary ls = gson.fromJson(body, LogbookSummary.class);
                return ls;

            } catch (Exception e) {
                Log.e("MOCK", e.toString());
                return null;
            }
        }
    };




}
