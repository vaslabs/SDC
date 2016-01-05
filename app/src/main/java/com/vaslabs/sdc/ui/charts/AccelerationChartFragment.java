package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class AccelerationChartFragment extends VelocityChartFragment {

    private AccelerationEntry[] accelerationEntries;

    @Override
    protected void getValues() {
        super.getValues();
        accelerationEntries = LogbookStats.calculateAccelerationValues(super.velocityEntries);
    }

    @Override
    public Entry[] getEntries() {
        return accelerationEntries;
    }
}
