package com.vaslabs.sdc.tests;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.utils.SkyDiver;

import android.test.AndroidTestCase;

public class TestSkyDiverSerialisation extends AndroidTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void test_that_skydiver_is_deserialised_into_correct_string_format() {
        UserPreferences up = new UserPreferences();
        up.mass=50f;
        up.name="Joe";
        up.seaLevel = 1014.12f;
        UserInformation.setUserPreferences( this.mContext, up );
        SkyDiver sd = new SkyDiver(UserInformation.getUserInfo( this.mContext ));
        
        assertEquals( "Joe:50.00|1014.12|null|null|null", sd.toString() );
    }
    
}
