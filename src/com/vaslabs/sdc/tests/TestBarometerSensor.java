package com.vaslabs.sdc.tests;

import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.MetersSensorValue;

import android.test.AndroidTestCase;

public class TestBarometerSensor extends AndroidTestCase {

    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void test_barometer_is_initialised() {
        BarometerSensor bs = new BarometerSensor(this.mContext);
        try {
            Thread.sleep( 1000 );
        } catch ( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertNotNull( bs.getValue());
        assertTrue(bs.getValue().hasBeenInitialised());
        double rawValue = bs.getValue().getRawValue();
        assertTrue(rawValue > 0);
    }
    
    public void test_barometer_converts_hPa_to_meters() {
        BarometerSensor bs = new BarometerSensor(this.mContext);
        try {
            Thread.sleep( 1000 );
        } catch ( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MetersSensorValue msv = bs.getAltitude();
        assertTrue(msv.getRawValue() > 0);
    }

}
