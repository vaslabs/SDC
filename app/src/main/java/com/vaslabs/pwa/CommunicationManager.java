package com.vaslabs.pwa;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaslabs.sdc.pwa.PWAInvalidCredentialsException;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CommunicationManager {

    private static CommunicationManager cm = null;
    private final Context context;
    private String apitoken = "";
    private final String host;
    private CommunicationManager(Context context) {
        host = context.getString(R.string.remote_host);
        this.context = context;
        try {
            apitoken = API.getApiToken(context);
        } catch (IOException e) {
            Log.e("CommunicationManager", e.toString());
        }
    }

    public static CommunicationManager getInstance(Context mContext) {

        if ( cm == null ) {
            cm = new CommunicationManager(mContext);
            try {
                cm.apitoken = API.getApiToken(mContext);
            } catch (IOException e) {
                Toast.makeText(mContext, R.string.api_token_problem, Toast.LENGTH_SHORT).show();
            }
        }
        return cm;
    }

    public static CommunicationManager getInstance() {
        return cm;
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

    public Response sendRequest( String jsonString, String location) throws Exception {

        URL url = new URL( this.host + location );
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

    public static Response submitLogs(String json, Context context) throws Exception {

        CommunicationManager cm = CommunicationManager.getInstance(context);
        return cm.sendRequest(json, "/dashboard/submit");

    }


    public Response sendRequest(String location) throws Exception {
        URL url = new URL( this.host + location );
        HttpURLConnection httpConnection =
                (HttpsURLConnection) url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty( "Authorization", "Token " + apitoken );

        TestPersistentConnection
                .setAcceptAllVerifier( (HttpsURLConnection) httpConnection );
        httpConnection.connect();

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
}
