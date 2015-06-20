import com.vaslabs.sdc.sensors.GPSSensor;

import android.location.Location;
import android.test.AndroidTestCase;

public class TestGPSManager extends AndroidTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void test_that_gps_data_exist() throws InterruptedException {
        GPSSensor gps = new GPSSensor(this.mContext);
        Thread.sleep( 1000 );
        Location l = gps.getCurrentLocation();
        for (int i = 0; i < 30 && l == null; i++) {
            Thread.sleep( 1000 );
            l = gps.getCurrentLocation();
        }
        assertNotNull( l );
    }
}
