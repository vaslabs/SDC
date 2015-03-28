package com.vaslabs.pwa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaslabs.sdc.pwa.PWAInvalidCredentialsException;

import android.util.Base64;

public class CommunicationManager {

    private static CommunicationManager cm = null;
    private String ip;
    private String username = "";
    private static String SIGN_IN_LOCATION = "/cgi-bin/sdc/sdc_logger_api.py";
    private String password = "";

    private CommunicationManager() {

    }

    public static CommunicationManager getInstance() {

        if ( cm == null ) {
            cm = new CommunicationManager();
        }
        return cm;
    }

    public void setRemoteHost( String IP ) {
        this.ip = IP;
    }

    public String getIP() {
        return this.ip;
    }

    public Response
            sendRequest( String location, HttpsURLConnection connection )
                    throws IOException, JSONException {
        String userpass = username + ":" + password;
        String basicAuth =
                "Basic "
                        + Base64.encodeToString( userpass.getBytes(),
                                Base64.DEFAULT );
        connection.setRequestProperty( "Authorization", basicAuth );

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

    public Response signIn( final String username, final String password )
            throws IOException, KeyManagementException,
            NoSuchAlgorithmException, JSONException {

        String location = ip + SIGN_IN_LOCATION;
        this.username = username;
        this.password = password;

        URL url = new URL( location );

        HttpURLConnection httpConnection =
                (HttpsURLConnection) url.openConnection();

        TestPersistentConnection
                .setAcceptAllVerifier( (HttpsURLConnection) httpConnection );

        return sendRequest( location, (HttpsURLConnection) httpConnection );

    }

    public Response sendRequest( String submitLocation, JSONObject submittableJSON,
            String username, String password ) throws Exception {
        this.username = username;
        this.password = password;
        String userpass = username + ":" + password;
        String basicAuth =
                "Basic "
                        + Base64.encodeToString( userpass.getBytes(),
                                Base64.DEFAULT );

        String location = ip + SIGN_IN_LOCATION;
        URL url = new URL( location );
        HttpURLConnection httpConnection =
                (HttpsURLConnection) url.openConnection();
        httpConnection.setDoOutput(true);
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty( "Authorization", basicAuth );
        
        TestPersistentConnection
                .setAcceptAllVerifier( (HttpsURLConnection) httpConnection );
        httpConnection.connect();

        OutputStreamWriter output = new OutputStreamWriter(httpConnection.getOutputStream());
        String jsonString = submittableJSON.toString();

        output.write( jsonString );
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

}
