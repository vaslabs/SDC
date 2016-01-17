package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.logs.LogbookStats;

import lecho.lib.hellocharts.model.Axis;

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

    @Override
    public Axis getYAxis() {
        Axis axis = new Axis();
        axis.setName("m/s*s");
        axis.setHasLines(true);
        return axis;
    }
}
