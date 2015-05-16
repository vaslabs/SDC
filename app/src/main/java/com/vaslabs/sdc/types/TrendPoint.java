package com.vaslabs.sdc.types;

import com.vaslabs.sdc.utils.Differentiable;

/**
 * Created by vnicolao on 16/05/15.
 */
public class TrendPoint<P extends Number, V extends Differentiable> implements Comparable<TrendPoint<P, V>> {
    public final V value;
    public final P point;
    public TrendPoint(V value, P point) {
        this.value = value;
        this.point = point;
    }

    @Override
    public int compareTo(TrendPoint<P, V> vpTrendPoint) {
        return Double.compare(point.doubleValue(), vpTrendPoint.point.doubleValue());
    }
}