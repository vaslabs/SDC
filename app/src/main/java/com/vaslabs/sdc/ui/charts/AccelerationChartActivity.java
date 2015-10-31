package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class AccelerationChartActivity extends VelocityChartActivity {

    private AccelerationEntry[] accelerationEntries;

    @Override
    protected void getValues() {
        super.getValues();
        accelerationEntries = LogbookStats.calculateAccelerationValues(super.velocityEntries);
    }

    @Override
    public AccelerationEntry[] getEntries() {
        return accelerationEntries;
    }
}
