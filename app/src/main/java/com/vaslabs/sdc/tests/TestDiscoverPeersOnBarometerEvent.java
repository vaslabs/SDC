package com.vaslabs.sdc.tests;

import android.test.AndroidTestCase;

import com.vaslabs.sdc.types.DifferentiableFloat;
import com.vaslabs.sdc.types.TrendPoint;
import com.vaslabs.sdc.utils.AbstractTrendStrategy;
import com.vaslabs.sdc.utils.BarometerTrendOnDiveAltitudeListener;
import com.vaslabs.sdc.utils.BarometerTrendStrategy;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.sdc.utils.TrendDirection;
import java.util.Map;

/**
 * Created by vnicolao on 16/05/15.
 */
public class TestDiscoverPeersOnBarometerEvent extends AndroidTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test_that_trend_detects_increament() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.5);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(1));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(1), new DifferentiableFloat(2));
        assertEquals(2, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(2), new DifferentiableFloat(3));
        assertEquals(3, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(3, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.UP, directionGraph.get(Double.valueOf(1)));
        assertEquals(TrendDirection.UP, directionGraph.get(Double.valueOf(2)));

    }

    public void test_that_trend_detects_decreament() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.5);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(3));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(1), new DifferentiableFloat(2));
        assertEquals(2, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(2), new DifferentiableFloat(1));
        assertEquals(3, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(3, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(1)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(2)));
    }

    public void test_time_density() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.1);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(3));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(0.09), new DifferentiableFloat(2));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(0.11), new DifferentiableFloat(1));
        assertEquals(2, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(2, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(0.11)));
    }

    int numbersCalled = 0;
    public void test_dive_scenario_without_perturbations() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.1, 3000);
        DefaultBarometerTrendListener trendListener = new DefaultBarometerTrendListener() {
            @Override
            public void onTrendEvent() {
                numbersCalled++;
            }
        };

        trendListener.forDirectionAction(TrendDirection.DOWN);
        trendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(1000f), Double.valueOf(0)));
        trendStrategy.registerEventListener(trendListener);
        uponClimbing(trendStrategy);
        assertEquals(0, numbersCalled);
        assertEquals(3000, trendStrategy.getSize());
        uponDiving(trendStrategy);
        assertEquals(3000, trendStrategy.getSize());
        assertEquals(1000, numbersCalled);


    }

    private void uponClimbing(AbstractTrendStrategy<DifferentiableFloat> trendStrategy) {
        for (int i = 0; i < 3000; i++) {
            trendStrategy.acceptValue(Double.valueOf(i), new DifferentiableFloat(Float.valueOf(i)));
        }
    }

    private void uponDiving(AbstractTrendStrategy<DifferentiableFloat> trendStrategy) {
        for (float i = 3000; i >= 0; i--) {
            trendStrategy.acceptValue(Double.valueOf(6000 - i), new DifferentiableFloat(i));
        }
    }

}
