package com.vaslabs.sdc.ui.charts;


import android.widget.Toast;

import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.charts.fragments.MainFragment;

public class BarometerChartFragment extends MainFragment {


    private BarometerEntries barometerEntries;
    protected BarometerEntry[] avgBarometerEntries;

    public static MainFragment newInstance(String param1, String param2) {
        BarometerChartFragment fragment = new BarometerChartFragment();
        return fragment;
    }

    @Override
    protected Entry[] getEntries() {
        return avgBarometerEntries;
    }

    @Override
    protected void getValues() {
        SkydivingSessionData latestSessionData = LogbookStats.getLatestSession(getActivity());
        this.barometerEntries = latestSessionData.getBarometerEntries();
        this.barometerEntries.sort();
        this.avgBarometerEntries = LogbookStats.average(this.barometerEntries, 1000);
        if (this.avgBarometerEntries.length < 100) {
            Toast.makeText(this.getActivity(), "This doesn't look like a skydiving session", Toast.LENGTH_SHORT).show();
        }
    }
}
