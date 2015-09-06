package com.vaslabs.acceptance;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.types.DifferentiableFloat;
import com.vaslabs.sdc.types.TrendPoint;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.utils.AbstractTrendStrategy;
import com.vaslabs.sdc.utils.BarometerTrendStrategy;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.sdc.utils.TrendDirection;


import java.io.InputStreamReader;

/**
 * Created by vnicolao on 20/06/15.
 */
public class TestBarometerTrendAlgorithm extends AndroidTestCase{


    BarometerEntries barometerEntries;
    private int numbersCalled = 0;

    @Override
    public void setUp() {
        Gson gson = new Gson();
        InputStreamReader jsonReader = new InputStreamReader(
                this.mContext.getResources().openRawResource(R.raw.barometer_test_data));
        barometerEntries = gson.fromJson(jsonReader, BarometerEntries.class);

        barometerEntries.sort();

        //ensure ascending
        assertTrue(barometerEntries.get(0).getTimestamp() < barometerEntries.get(5).getTimestamp());
    }

    float currentHeight = 0;
    long currentTime = 0;
    int callEmergencyChecker = 0;
    public void test_acceptance() {
        final AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(50, 1.0, 50);
        DefaultBarometerTrendListener trendListener = new DefaultBarometerTrendListener() {
            @Override
            public void onTrendEvent() {
                numbersCalled++;
                assertTrue(currentHeight < 1000);
                Log.i("trend event", String.valueOf(currentTime));
            }
        };

        DefaultBarometerTrendListener landingTrendListener = new DefaultBarometerTrendListener() {
            @Override
            public void onTrendEvent() {
                callEmergencyChecker++;
            }
        };

        landingTrendListener.forDirectionAction(TrendDirection.DOWN);
        landingTrendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(150f), Double.valueOf(0)));
        trendStrategy.registerEventListener(landingTrendListener);

        trendListener.forDirectionAction(TrendDirection.DOWN);
        trendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(1000f), Double.valueOf(0)));
        trendStrategy.registerEventListener(trendListener);
        BarometerEntry nextEntry;
        for (int i = 0; i < barometerEntries.size(); i++) {
            nextEntry = barometerEntries.get(i);
            currentTime = nextEntry.getTimestamp();
            currentHeight = nextEntry.getAltitude();
            trendStrategy.acceptValue(nextEntry.getTimestamp()/1000.0,
                    new DifferentiableFloat(nextEntry.getAltitude()));

        }

        assertTrue(numbersCalled > 0);
        assertEquals(1, callEmergencyChecker);

    }
}
