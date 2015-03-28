package com.vaslabs.sdc.tests;


import org.json.JSONException;
import org.json.JSONObject;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.pwa.PWAInvalidCredentialsException;
import com.vaslabs.sdc.ui.R;

import android.test.AndroidTestCase;

public class TestRemoteServerForSavingSDCLogs extends AndroidTestCase {

    private String username;
    private String password;
    
    public void setUp() {
        username = mContext.getString( R.string.username );
        password = mContext.getString( R.string.password );
    }
    
    public void test_reachability() {
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( mContext.getString( R.string.remote_host ));
        try {
            Response response = cm.signIn( username, password );
            Object body = response.getBody();
            assertTrue(body instanceof JSONObject);
            JSONObject jsBody = (JSONObject)body;
            assertEquals("INVALID DATA", jsBody.getString( "message" ));
        } catch ( Exception e ) {
           fail(e.toString());
        }
    }
    
    public void test_that_logs_can_be_submitted() throws JSONException {
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( mContext.getString( R.string.remote_host ));
        SDCLogManager lm = SDCLogManager.getInstance(mContext);
        try {
            lm.submitLogs(username, password);
        }
        catch (Exception e) {
            fail(e.toString());
        }
        Response response = lm.getLastResponse();
        Object responseBody = response.getBody();
        assertTrue(responseBody instanceof JSONObject);
        JSONObject json = (JSONObject)responseBody;
        assertEquals("OK", json.getString("message"));
        
        
        try {
            lm.submitLogs("username", "password");
            
        }
        catch (Exception ice) {
            if (ice instanceof PWAInvalidCredentialsException) {
                return;
            }
            fail(ice.toString());
        }
        
    }
    
    
    
}
