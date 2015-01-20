package com.vaslabs.sdc.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;

import android.content.Context;
import android.hardware.SensorManager;
import android.test.AndroidTestCase;

public class TestUserPreferences extends AndroidTestCase {
    private static final String USER_INFO_FILE_NAME = "ui.info";
    protected void setUp() throws Exception {
        super.setUp();
        FileOutputStream fos = null;

        try {
            fos = this.mContext.openFileOutput( USER_INFO_FILE_NAME,
                            Context.MODE_PRIVATE );
            try {
                fos.write( "".getBytes() );
            } catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch ( IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch ( FileNotFoundException e1 ) {

        }
    }
    public void test_default_user_preferences() {
        UserInformation ui = UserInformation.getUserInfo( this.mContext );
        assertEquals( ui.getMass(), 50f );
        assertEquals(ui.getSeaLevelCalibration(), SensorManager.PRESSURE_STANDARD_ATMOSPHERE);
    }
    public void test_modifying_user_preferences() {
        UserPreferences up = new UserPreferences();
        up.mass = 70f;
        up.seaLevel = 1000f;
        UserInformation.setUserPreferences( this.mContext, up );
        UserInformation ui = UserInformation.getUserInfo( this.mContext );
        assertEquals( ui.getMass(), 70f );
        assertEquals(ui.getSeaLevelCalibration(), 1000f);
        
    }
}
