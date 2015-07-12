package com.vaslabs.sdc.logs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.structs.DateStruct;
public class SDCLogManager {

    private static final int MIN_PERMITTED_SIZE = 200;
    private static SDCLogManager logManager = new SDCLogManager();

    private static final String LATEST_SESSION_JSON_FILE = "latest.json";

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
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ioe) {

                }
        }
        strings.add("=END OF CONNECTIONS=");
        strings.addAll(SkyDivingEnvironment.getBarometerSensorLogsLinesUncompressed(context));
        strings.add("=END OF BAROMETER=");
        strings.addAll(SkyDivingEnvironment.getGPSSensorLogsLinesUncompressed(context));
        strings.add("=END OF GPS=");

        return strings;

    }

    private String buildRequest( SkydivingSessionData sessionData ) throws JSONException {
        Gson gson = new Gson();
        return gson.toJson(sessionData);
    }

    public Response[] getResponses() {
        return this.responses;
    }

    public void submitLogs(Map<DateStruct, SkydivingSessionData> sessionDates) throws Exception {
        Set<DateStruct> dates = sessionDates.keySet();
        SkydivingSessionData sessionData;
        String json;
        Response[] responses = new Response[dates.size()];
        int counter = 0;
        for (DateStruct key : dates) {
            sessionData = sessionDates.get(key);
            if (sessionData.gpsEntriesSize() + sessionData.barometerEntriesSize() < MIN_PERMITTED_SIZE) {
                responses[counter++] = Response.SKIPPED_RESPONSE;
                continue;
            }
            json = buildRequest(sessionData);
            responses[counter++] = CommunicationManager.submitLogs(json, this.context);
        }
        this.responses = responses;
        sessionDates.clear();
    }

    public void saveLatestSession(SkydivingSessionData sessionData) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(sessionData);
        FileOutputStream fos = context.openFileOutput(LATEST_SESSION_JSON_FILE, Context.MODE_PRIVATE);

        PrintWriter writer = new PrintWriter(fos);
        writer.write(jsonString);
        writer.close();
    }

    public void manageLogSubmission() throws Exception {
        String jsonString = LogUtils.parse(this.loadLogs());
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        sessionData = SessionFilter.mostRecent(sessionDates);
        SDCLogManager logManager = SDCLogManager.getInstance(context);
        logManager.submitLogs(sessionDates);
        try {
            this.saveLatestSession(sessionData);
        } catch (IOException ioe) {
            Log.e("SDLCLogManager", ioe.toString());
        }

        this.clearLogs();
    }

    private void clearLogs() {
        context.deleteFile(PositionGraph.BAROMETER_LOG_FILE);
        context.deleteFile(PositionGraph.GPS_LOG_FILE);
        context.deleteFile(SkyDivingEnvironment.getLogFile());
    }
}
