package com.vaslabs.sdc.ui.util;

import com.vaslabs.sdc.utils.Trend;

/**
 * Created by vnicolao on 16/05/15.
 */
public final class TrendingPreferences {

    public final float altitudeSensitivity;
    public final double timeDensity;

    private static TrendingPreferences trendingPreferences = new TrendingPreferences(50, 0.5);
    private static Object lock = new Object();
    public static float altitudeLimit;

    private TrendingPreferences(float altitudeSensitivity, double timeDensity) {
        this.altitudeSensitivity = altitudeSensitivity;
        this.timeDensity = timeDensity;
    }

    public static TrendingPreferences getNewTrendingPreferences(float altitudeSensitivity, double timeDensity) {
        synchronized (lock) {
            trendingPreferences = new TrendingPreferences(altitudeSensitivity, timeDensity);
            return trendingPreferences;
        }
    }

    public static TrendingPreferences getInstance() {
        return trendingPreferences;
    }
}
