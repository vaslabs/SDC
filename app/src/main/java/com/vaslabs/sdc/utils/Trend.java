package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.types.TrendPoint;

import java.util.List;

public interface Trend<V extends Number, P extends Differentiable> {
    void acceptValue(V value, P point);
    void getValueAt(P point);
    List<TrendPoint> getNormalisedTrend();
}
