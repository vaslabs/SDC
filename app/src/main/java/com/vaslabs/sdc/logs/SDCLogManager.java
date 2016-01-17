package com.vaslabs.sdc.logs;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.cache.CacheManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;
import com.vaslabs.structs.DateStruct;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class SDCLogManager {

    private static final int MIN_PERMITTED_SIZE = 200;
    private static SDCLogManager logManager = new SDCLogManager();

    private Context context = null;
    private Response[] responses;

    public static SDCLogManager getInstance( Context mContext ) {
        synchronized ( logManager ) {
            if ( logManager.context == null ) {
                logManager.context = mContext;
            }
        }
        return logManager;
    }


    public List<String> loadLogs() throws IOException {
        FileInputStream inputStream = null;
        List<String> strings = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            inputStream = context.openFileInput(SkyDivingEnvironment.getLogFile());
            reader =
                    new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
        } catch (FileNotFoundException fnfe) {

        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ioe) {

                }
        }
        strings.add("=END OF CONNECTIONS=");
        List<String> barometerLines = SkyDivingEnvironment.getBarometerSensorLogsLinesUncompressed(context);
        if (barometerLines != null)
            strings.addAll(barometerLines);
        strings.add("=END OF BAROMETER=");
        List<String> gpsLines = SkyDivingEnvironment.getGPSSensorLogsLinesUncompressed(context);
        if (gpsLines != null)
            strings.addAll(gpsLines);
        strings.add("=END OF GPS=");
        if (gpsLines == null && barometerLines == null)
            throw new IOException("No data");
        return strings;

    }

    private String buildRequest( SkydivingSessionData sessionData ) {
        Gson gson = new Gson();
        return gson.toJson(sessionData);
    }

    public Response[] getResponses() {
        return this.responses;
    }

    private Map<DateStruct, SkydivingSessionData> manageLogSubmission() throws IOException {
        String jsonString = LogUtils.parse(loadLogs());
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        return sessionDates;
    }

    public void clearLogs() {
        context.deleteFile(PositionGraph.BAROMETER_LOG_FILE);
        context.deleteFile(PositionGraph.GPS_LOG_FILE);
        context.deleteFile(SkyDivingEnvironment.getLogFile());
    }

    private int submitted = 0;
    private int containsUpTo = 0;


    private Response.Listener<JSONObject> submittedListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            submitted++;
            Toast.makeText(context, context.getString(R.string.submitted) + submitted, Toast.LENGTH_SHORT).show();
            if (submitted >= containsUpTo) {
                clearLogs();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse (VolleyError error){
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    public void submitLogs(SdcService sdcService) {
        try {
            String apiToken = API.getApiToken(context);
            Map<DateStruct, SkydivingSessionData> logs = this.manageLogSubmission();
            if (logs == null || logs.size() == 0) {
                Toast.makeText(context, context.getString(R.string.no_logs), Toast.LENGTH_SHORT).show();
            }
            containsUpTo = logs.size();
            for (DateStruct ds : logs.keySet()) {
                SkydivingSessionData skydivingSessionData = logs.get(ds);
                if (skydivingSessionData.barometerEntriesSize() < 200)
                    continue;
                String jsonData = buildRequest(skydivingSessionData);
                sdcService.submitSession(apiToken, jsonData, submittedListener, errorListener);
            }
            CacheManager.clearCache(context, apiToken);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
