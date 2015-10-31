package com.vaslabs.sdc.ui.charts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.ui.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;


public class BarometerChartActivity extends ChartActivity {


    private BarometerEntries barometerEntries;
    protected BarometerEntry[] avgBarometerEntries;

    @Override
    protected Entry[] getEntries() {
        return avgBarometerEntries;
    }

    @Override
    protected void getValues() {
        Gson gson = new Gson();
        InputStreamReader jsonReader = null;
        try {
            jsonReader = new InputStreamReader(
                    this.openFileInput(SDCLogManager.LATEST_SESSION_JSON_FILE));
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No latest activity found!", Toast.LENGTH_SHORT).show();
        }
        SkydivingSessionData latestSessionData = gson.fromJson(jsonReader, SkydivingSessionData.class);
        try {
            jsonReader.close();
        } catch (IOException e) {

        }
        this.barometerEntries = latestSessionData.getBarometerEntries();
        this.avgBarometerEntries = LogbookStats.average(this.barometerEntries, 1000);
        if (this.avgBarometerEntries.length < 100) {
            Toast.makeText(this, "This doesn't look like a skydiving session", Toast.LENGTH_SHORT).show();
        }
    }
}
