package com.vaslabs.sdc.tests;


import com.vaslabs.pwa.CommunicationManager;
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
            cm.signIn( username, password );
        } catch ( Exception e ) {
           fail(e.toString());
        }
    }
    
    
    
}
