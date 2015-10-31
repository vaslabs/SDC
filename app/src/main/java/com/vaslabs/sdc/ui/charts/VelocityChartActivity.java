package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.VelocityEntry;
import com.vaslabs.sdc.logs.LogbookStats;


public class VelocityChartActivity extends BarometerChartActivity {

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
}
