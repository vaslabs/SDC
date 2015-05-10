package com.vaslabs.sdc.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrendStrategy<P extends Differentiable> implements Trend<Double, P> {


    private List<TrendPoint<Double, P>> trendPoints;
    private boolean trendPointsAreSorted = true;
    private final Map<P, Double> trendMap;
    private final Map<P, TrendDirection> trendDirectionMap;
    private final Map<P, VelocityState> trendVelocityStateMap;
    private TrendDirection currentDirection = null;
    private VelocityState currentVelocityState = null;
    private final double accuracy;
    private final long timeDensity;
    private final int historySize;
    public AbstractTrendStrategy(double accuracy, long timeDensity) {
        this(accuracy, timeDensity, 16);
    }

    public AbstractTrendStrategy(double accuracy, long density, int historySize) {
        this.historySize = historySize;
        this.accuracy = accuracy;
        this.timeDensity = density;
        trendPoints = new ArrayList<TrendPoint<Double, P>>();
        trendMap = new HashMap<P, Double>();
        trendDirectionMap = new HashMap<P, TrendDirection>();
        trendVelocityStateMap = new HashMap<P, VelocityState>();
    }

    public final synchronized void acceptValue(Double value, P point) {
        if (!trendMap.containsKey(point)) {
            trendMap.put(point, value);
            TrendPoint tp = new TrendPoint<Double, P>(value, point);
            trendPoints.add(tp);
            int noOfTrendItems = trendPoints.size();
            if (noOfTrendItems > 1)
            if (tp.compareTo(trendPoints.get(noOfTrendItems - 2)) < 0) {
                trendPointsAreSorted = false;
            }
            applyTrendActions();
        }
    }

    public final synchronized boolean isSorted() {
        return trendPointsAreSorted;
    }

    private final void applyTrendActions() {
        normalise();
        int size = trendPoints.size();
        if (size == 0)
            return;
        if (size == 1)
            return;

        double previousValue = trendPoints.get(size - 2).value;
        double currentValue = trendPoints.get(size - 1).value;
        double difference = currentValue - previousValue;
        if (difference > accuracy) {
            currentDirection = TrendDirection.UP;
        } else if (difference >= 0) {
            currentDirection = TrendDirection.NEUTRAL;
        } else if (difference < -accuracy) {
            currentDirection = TrendDirection.DOWN;
        } else {
            currentDirection = TrendDirection.NEUTRAL;
        }
        trendDirectionMap.put(trendPoints.get(size - 1).point, currentDirection);

        computeAcceleration();
        onTrendUpdate();

    }

    protected abstract void onTrendUpdate();

    public final List<TrendPoint<Double, P>> getGraph(int startFrom, int endAt) {
        arrangeSort();
        List<TrendPoint<Double, P>> trend = new ArrayList<TrendPoint<Double, P>>();
        if (startFrom < 0)
            return trend;
        if (endAt > trendPoints.size())
            return trend;
        for (int i = startFrom; i < endAt; i++) {
            trend.add(trendPoints.get(i));
        }

        return trend;
    }

    public List<TrendPoint<Double, P>> getGraph(int latest) {
        int startFrom = historySize - latest;
        return getGraph(startFrom, historySize);
    }

    private final void computeAcceleration() {
        int size = trendPoints.size();
        if (size < 3)
            return;

        double velocityFromAtoB = computeVelocity(trendPoints.get(size - 3), trendPoints.get(size - 2));
        double velocityFromBtoA = computeVelocity(trendPoints.get(size -2), trendPoints.get(size - 1));
        double dt = trendPoints.get(size - 3).point.differantiate(trendPoints.get(size - 1).point);
        double acceleration = (velocityFromBtoA - velocityFromAtoB) / dt;

        if (acceleration > accuracy) {
            currentVelocityState = VelocityState.ACCELERATED;
        } else if (acceleration > 0) {
            currentVelocityState = VelocityState.CONSTANT;
        } else if (acceleration < -accuracy) {
            currentVelocityState = VelocityState.DECELERATED;
        } else {
            currentVelocityState = VelocityState.CONSTANT;
        }

        trendVelocityStateMap.put(trendPoints.get(size - 1).point, currentVelocityState);

    }

    private final double computeVelocity(TrendPoint<Double, P> pointA, TrendPoint<Double, P> pointB) {
        return
                (pointA.point.differantiate(pointB.point)) /
                        (pointA.value - pointB.value);
    }

    private void normalise() {
        arrangeSort();
        clearFromHistory();
        applyFilters();
    }

    private final synchronized void clearFromHistory() {
        if (trendPoints.size() <= historySize) {
            return;
        }
        List<TrendPoint<Double, P>> trendPointsLimit = new ArrayList<TrendPoint<Double, P>>(historySize + 2);
        TrendPoint tp;
        for (int i = 0; i < trendPoints.size() - historySize; i++) {
            tp = trendPoints.get(i);
            trendDirectionMap.remove(tp.point);
            trendVelocityStateMap.remove(tp.point);
            trendMap.remove(tp.point);
        }
        for (int i = trendPoints.size() - historySize; i < trendPoints.size(); i++) {
            tp = trendPoints.get(i);
            trendPointsLimit.add(tp);
        }
        trendPoints = trendPointsLimit;

    }

    protected abstract void applyFilters();

    private void arrangeSort() {
        if (!isSorted()) {
            Collections.sort(trendPoints);
            trendPointsAreSorted = true;
        }
    }


}
