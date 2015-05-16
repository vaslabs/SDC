package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;

/**
 * Created by vnicolao on 16/05/15.
 */
public class BarometerTrendOnDiveAltitudeListener extends DefaultBarometerTrendListener {



    @Override
    public void onTrendEvent() {
        SkyDivingEnvironment.getInstance().beginScanning();
    }




}
