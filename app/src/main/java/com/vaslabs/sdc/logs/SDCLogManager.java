package com.vaslabs.sdc.logs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;

public class SDCLogManager {

    private static SDCLogManager logManager = new SDCLogManager();
    private Response lastResponse = null;

    private Context context = null;
    private final String SUBMIT_LOCATION = "/cgi-bin/sdc/sdc_logger_api.py";

    public static SDCLogManager getInstance( Context mContext ) {
        synchronized ( logManager ) {
            if ( logManager.context == null ) {

                logManager.context = mContext;
            }
        }
        return logManager;
    }

    public void submitLogs( String username, String password ) throws Exception {
        List<String> logs = null;
        try {
            logs = loadLogs();
            JSONObject submittableJSON = buildRequest(logs);
            CommunicationManager cm = CommunicationManager.getInstance();
            lastResponse = cm.sendRequest( SUBMIT_LOCATION, submittableJSON, username, password );
        } catch (FileNotFoundException fnfe) {
            throw new NoLogsAvailableException();
        }
    }

    private JSONObject buildRequest( List<String> logs ) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray jsonLines = new JSONArray();
        for (String line : logs) {
            jsonLines.put( line );
        }
        json.accumulate("log", jsonLines);
        
        return json;
    }

    public List<String> loadLogs() throws FileNotFoundException, IOException {
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
        } catch (IOException ioe) {}
        finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ioe) {

                }
        }
        strings.add("=END OF CONNECTIONS=");
        strings.addAll(SkyDivingEnvironment.getInstance(context).getBarometerSensorLogsLinesUncompressed());
        strings.add("=END OF BAROMETER=");
        strings.addAll(SkyDivingEnvironment.getInstance(context).getGPSSensorLogsLinesUncompressed());
        strings.add("=END OF GPS=");

        return strings;

    }

    public Response getLastResponse() {
        return lastResponse;
    }

}
