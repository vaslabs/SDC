package com.vaslabs.logbook;

import android.content.Context;

import com.google.gson.Gson;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc_dashboard.API.API;

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
    };




}
