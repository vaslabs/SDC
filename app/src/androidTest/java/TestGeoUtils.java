import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.GeoUtils;
import com.vaslabs.sdc.utils.Position;

import android.test.AndroidTestCase;

public class TestGeoUtils extends AndroidTestCase {
    
    
    public void test_geo_utils_distance_between_two_positions() {
        LatitudeSensorValue latA = new LatitudeSensorValue(41.756192);
        LongitudeSensorValue lngA = new LongitudeSensorValue(87.967360);
        MetersSensorValue altA = new MetersSensorValue(192.0f);

        
        Position a = new Position(lngA, latA, altA);
        
        LatitudeSensorValue latB = new LatitudeSensorValue(41.756192);
        LongitudeSensorValue lngB = new LongitudeSensorValue(87.967360);
        MetersSensorValue altB = new MetersSensorValue(198.0f);
        
        Position b = new Position(lngB, latB, altB);
        
        double distance = GeoUtils.calculateDistance( a, b );
        
        assertEquals(6, distance, 0.1);

        latB = new LatitudeSensorValue(41.758701);
        lngB = new LongitudeSensorValue(87.973307);
        altB = new MetersSensorValue(198.0f);

        
        b = new Position(lngB, latB, altB);
        distance = GeoUtils.calculateDistance( a, b );
        
        assertEquals(567, distance, 1);

        
        
    }
    
}
