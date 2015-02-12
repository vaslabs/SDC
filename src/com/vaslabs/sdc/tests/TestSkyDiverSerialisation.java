package com.vaslabs.sdc.tests;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.sdc.utils.SkyDiver;

import android.test.AndroidTestCase;

public class TestSkyDiverSerialisation extends AndroidTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test_that_skydiver_is_deserialised_into_correct_string_format() {
        UserPreferences up = new UserPreferences();
        up.mass = 50f;
        up.name = "Joe";
        up.seaLevel = 1014.12f;
        UserInformation.setUserPreferences( this.mContext, up );
        SkyDiver sd =
                new SkyDiver( UserInformation.getUserInfo( this.mContext ) );

        assertEquals( "Joe:50.00|1014.12|null|null|null", sd.toString() );

        LongitudeSensorValue longitude = new LongitudeSensorValue();
        longitude.setRawValue( 102.131 );
        LatitudeSensorValue latitude = new LatitudeSensorValue();
        latitude.setRawValue( 202.121 );
        MetersSensorValue altitude = new MetersSensorValue();
        altitude.setRawValue( 1000f );
        Position newPosition = new Position( longitude, latitude, altitude );

        sd.updatePositionInformation( newPosition );
        String expectedValue =
                String.format( "Joe:50.00|1014.12|%f|%f|%.2f",
                        latitude.getRawValue(), longitude.getRawValue(),
                        altitude.getRawValue() );
        String sdValue = sd.toString();
        assertEquals( expectedValue, sdValue );

    }

    public void test_that_skydiver_string_is_serialised_into_skydiver_object() {
        String deserialisedString = "Joe:50.00|1014.12|202.121|102.131|1000.00";
        SkyDiver sd = SkyDiver.serialiseSkyDiverFromString( deserialisedString );
        assertNotNull(sd);
        assertEquals("Joe", sd.getName());
        Position skyDiverPosition = sd.getPosition();
        assertEquals( 50, sd.getMass(), 0.001);
        assertEquals(1014.12, sd.getCustomSeaLevelCalibration(), 0.001);
        assertEquals(202.121, skyDiverPosition.getLat().getRawValue(), 0.001);
        assertEquals(102.131, skyDiverPosition.getLng().getRawValue(), 0.001);

        assertEquals(1000, skyDiverPosition.getAlt().getRawValue(), 0.001);

    }
    
    public void test_that_skydiver_string_is_serialised_into_skydiver_object_edge_case() {
        String deserialisedString = "Joe:50.00|1014.12|null|null|null";
        SkyDiver sd = SkyDiver.serialiseSkyDiverFromString( deserialisedString );
        assertNotNull(sd);
        assertEquals("Joe", sd.getName());
        Position skyDiverPosition = sd.getPosition();
        assertNull( skyDiverPosition);
        
    }

}
