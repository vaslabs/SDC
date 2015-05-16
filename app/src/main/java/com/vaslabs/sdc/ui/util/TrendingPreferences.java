package com.vaslabs.sdc.ui.util;

/**
 * Created by vnicolao on 16/05/15.
 */
public final class TrendingPreferences {

    public final float altitudeSensitivity;
    public final double timeDensity;

    private static TrendingPreferences trendingPreferences = new TrendingPreferences(50, 0.5, 1000);
    private static Object lock = new Object();
    public final float altitudeLimit;

    private TrendingPreferences(float altitudeSensitivity, double timeDensity, float altitudeLimit) {
        this.altitudeSensitivity = altitudeSensitivity;
        this.timeDensity = timeDensity;
        this.altitudeLimit = altitudeLimit;
    }

    public static TrendingPreferences getNewTrendingPreferences(float altitudeSensitivity, double timeDensity, float altitudeLimit) {
        synchronized (lock) {
            trendingPreferences = new TrendingPreferences(altitudeSensitivity, timeDensity, altitudeLimit);
            return trendingPreferences;
        }
    }

    public static TrendingPreferences getInstance() {
        return trendingPreferences;
    }
}
