package com.vaslabs.sdc.ui.charts;

import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GForceEntry;
import com.vaslabs.sdc.logs.LogbookStats;

import lecho.lib.hellocharts.model.Axis;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class GforceChartFragment extends AccelerationChartFragment {

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

    @Override
    public Axis getYAxis() {
        Axis axis = new Axis();
        axis.setName("G's");
        axis.setHasLines(true);
        return axis;
    }

}
