package com.vaslabs.pwa;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaslabs.sdc.pwa.PWAInvalidCredentialsException;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;

import android.content.Context;
import android.util.Base64;

public class CommunicationManager {

    private static CommunicationManager cm = null;
    private String location;
    private String apitoken;

    private CommunicationManager() {

    }

    public static CommunicationManager getInstance() {

        if ( cm == null ) {
            cm = new CommunicationManager();
        }
        return cm;
    }

    public void setRemoteHost( String location ) {
        this.location = location;
    }

    public Response
            sendRequest( HttpsURLConnection connection )
                    throws IOException, JSONException {

        connection.setRequestProperty( "Authorization", "Token " + apitoken);

        connection.connect();
        InputStream is = connection.getInputStream();

        BufferedReader streamReader =
                new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        responseStrBuilder.append( "[" );
        while ( ( inputStr = streamReader.readLine() ) != null )
            responseStrBuilder.append( inputStr );
        responseStrBuilder.append( "]" );

        streamReader.close();
        JSONArray json = null;
        json = new JSONArray( responseStrBuilder.toString() );

        Response res = new Response( json, connection.getResponseCode() );
        return res;

    }

    public Response sendRequest( String jsonString, String apitoken) throws Exception {

        URL url = new URL( location );
        HttpURLConnection httpConnection =
                (HttpsURLConnection) url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty( "Authorization", "Token " + apitoken );
        
        TestPersistentConnection
                .setAcceptAllVerifier( (HttpsURLConnection) httpConnection );
        httpConnection.connect();

        OutputStreamWriter output = new OutputStreamWriter(httpConnection.getOutputStream());

        output.write(jsonString );
        output.close();

        int responseCode = httpConnection.getResponseCode();
        
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
            throw new PWAInvalidCredentialsException();
        
        InputStream is = httpConnection.getInputStream();

        String inputStr;
        BufferedReader streamReader =
                new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
        StringBuilder responseStrBuilder = new StringBuilder();
        responseStrBuilder.append( "[" );
        while ( ( inputStr = streamReader.readLine() ) != null )
            responseStrBuilder.append( inputStr );
        responseStrBuilder.append( "]" );

        streamReader.close();
        JSONArray json = null;
        json = new JSONArray( responseStrBuilder.toString() );

        Response res = new Response( json, httpConnection.getResponseCode() );
        httpConnection.disconnect();
        
        return res;
        
        
    }

    public static void submitLogs(String json, Context context) throws Exception {
        String token = API.getApiToken(context);

        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost(context.getString(R.string.remote_host));
        cm.sendRequest(json, token);

    }


}
