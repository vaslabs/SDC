package com.vaslabs.sdc.tests;

import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.GeoUtils;
import com.vaslabs.sdc.utils.Position;

import android.test.AndroidTestCase;

public class TestGeoUtils extends AndroidTestCase {
    
    
    public void test_geo_utils_distance_between_two_positions() {
        LatitudeSensorValue latA = new LatitudeSensorValue();
        LongitudeSensorValue lngA = new LongitudeSensorValue();
        MetersSensorValue altA = new MetersSensorValue();
        
        latA.setRawValue( 41.756192 );
        lngA.setRawValue( 87.967360 );
        altA.setRawValue( 192.0f );
        
        Position a = new Position(lngA, latA, altA);
        
        LatitudeSensorValue latB = new LatitudeSensorValue();
        LongitudeSensorValue lngB = new LongitudeSensorValue();
        MetersSensorValue altB = new MetersSensorValue();
        
        latB.setRawValue( 41.756192 );
        lngB.setRawValue( 87.967360 );
        altB.setRawValue( 198.0f );
        
        Position b = new Position(lngB, latB, altB);
        
        double distance = GeoUtils.calculateDistance( a, b );
        
        assertEquals(6, distance, 0.1);
        
    }
    
}
