package com.vaslabs.sdc.utils;


import com.vaslabs.vtrends.impl.AbstractTrendStrategy;
import com.vaslabs.vtrends.types.DifferentiableFloat;
import com.vaslabs.vtrends.types.TrendPoint;

import java.util.List;

/**
 * Created by vnicolao on 16/05/15.
 */
public class BarometerTrendStrategy<T> extends AbstractTrendStrategy<DifferentiableFloat> {


    public BarometerTrendStrategy(double accuracy, long timeDensity) {
        super(accuracy, timeDensity);
    }

    public BarometerTrendStrategy(double accuracy, double timeDensity) {
        super(accuracy, timeDensity);
    }

    public BarometerTrendStrategy(double accuracy, double timeDensity, int historySize) {
        super(accuracy, timeDensity, historySize);
    }


    @Override
    protected void applyFilters() {

    }

    @Override
    public void getValueAt(DifferentiableFloat point) {

    }

    @Override
    public List<TrendPoint> getNormalisedTrend() {
        return null;
    }

}
