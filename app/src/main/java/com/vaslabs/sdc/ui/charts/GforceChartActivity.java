package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GForceEntry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class GforceChartActivity extends AccelerationChartActivity {

    private GForceEntry[] gForceEntries;

    @Override
    public void getValues() {
        super.getValues();
        gForceEntries = LogbookStats.calculateGForce(super.getEntries());
    }

    @Override
    public Entry[] getEntries() {
        return gForceEntries;
    }

}
