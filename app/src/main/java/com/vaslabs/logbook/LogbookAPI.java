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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public enum LogbookAPI implements ILogbookAPI {
    INSTANCE {

        @Override
        public List<Logbook> getLogbookEntries() throws Exception {
            CommunicationManager cm = CommunicationManager.getInstance();
            Response response = cm.sendRequest(LOGBOOK_API);
            Gson gson = new Gson();
            Logbook[] logbookArray = gson.fromJson(response.getBody().toString(), Logbook[].class);
            List<Logbook> logbookList = Arrays.asList(logbookArray);
            return logbookList;
        }
    },
    MOCK {
        @Override
        public List<Logbook> getLogbookEntries() {
            return null;
        }
    };

    private static final String LOGBOOK_API = "/logbook/api_get/";

}
