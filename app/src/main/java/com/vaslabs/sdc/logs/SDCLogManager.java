package com.vaslabs.sdc.logs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.structs.DateStruct;

public class SDCLogManager {

    private static SDCLogManager logManager = new SDCLogManager();
    private Response lastResponse = null;

    private static final String LATEST_SESSION_JSON_FILE = "latest.json";

    private Context context = null;

    public static SDCLogManager getInstance( Context mContext ) {
        synchronized ( logManager ) {
            if ( logManager.context == null ) {

                logManager.context = mContext;
            }
        }
        return logManager;
    }

    private String buildRequest( SkydivingSessionData sessionData ) throws JSONException {
        Gson gson = new Gson();
        return gson.toJson(sessionData);
    }

    public InputStream openLogs() throws IOException {
        FileInputStream inputStream = null;

        inputStream = context.openFileInput(SkyDivingEnvironment.getLogFile());
        return inputStream;

    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void submitLogs(Map<DateStruct, SkydivingSessionData> sessionDates) throws Exception {
        Set<DateStruct> dates = sessionDates.keySet();
        SkydivingSessionData sessionData;
        String json;
        for (DateStruct key : dates) {
            sessionData = sessionDates.get(key);
            json = buildRequest(sessionData);
            CommunicationManager.submitLogs(json, this.context);
            sessionDates.remove(key);
        }
    }

    public void saveLatestSession(SkydivingSessionData sessionData) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(sessionData);
        FileOutputStream fos = context.openFileOutput(LATEST_SESSION_JSON_FILE, Context.MODE_PRIVATE);

        PrintWriter writer = new PrintWriter(fos);
        writer.write(jsonString);
    }
}
