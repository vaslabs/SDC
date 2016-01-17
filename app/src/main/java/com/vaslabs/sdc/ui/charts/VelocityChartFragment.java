package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.VelocityEntry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.charts.fragments.MainFragment;

import lecho.lib.hellocharts.model.Axis;


public class VelocityChartFragment extends BarometerChartFragment {

    protected VelocityEntry[] velocityEntries;

    @Override
    protected void getValues() {
        super.getValues();
        this.velocityEntries = LogbookStats.calculateVelocityValues(this.avgBarometerEntries, 8000);
    }

    @Override
    public Entry[] getEntries() {
        return this.velocityEntries;
    }

    @Override
    public Axis getYAxis() {
        Axis axis = new Axis();
        axis.setName("m/s");
        return axis;
    }
}
