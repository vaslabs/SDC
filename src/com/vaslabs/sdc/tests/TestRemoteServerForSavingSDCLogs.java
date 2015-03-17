package com.vaslabs.sdc.tests;


import org.json.JSONObject;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
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
    
    
    
}
