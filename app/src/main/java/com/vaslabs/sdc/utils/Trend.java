package com.vaslabs.sdc.utils;

import java.util.List;

public interface Trend<V extends Number, P extends Differentiable> {
    void acceptValue(V value, P point);
    void getValueAt(P point);
    List<TrendPoint> getNormalisedTrend();
}

final class TrendPoint<V extends Number, P extends Differentiable> implements Comparable<TrendPoint<V, P>> {
    public final V value;
    public final P point;
    public TrendPoint(V value, P point) {
        this.value = value;
        this.point = point;
    }

    @Override
    public int compareTo(TrendPoint<V, P> vpTrendPoint) {
        return this.point.compareTo(vpTrendPoint.point);
    }
}

enum TrendDirection {UP, DOWN, NEUTRAL};
enum VelocityState {ACCELERATED, DECELERATED, CONSTANT};
