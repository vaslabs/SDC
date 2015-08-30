package com.vaslabs.sdc.utils;

import com.vaslabs.emergency.StrategyVisitor;
import com.vaslabs.sdc.types.TrendPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrendStrategy<V extends Differentiable> implements Trend<Double, V> {


    private ArrayList<TrendPoint<Double, V>> trendPoints;
    private int cycleIndex = 0;
    private boolean isCyclic = false;
    private final Map<Double, V> trendMap;
    private final Map<Double, TrendDirection> trendDirectionMap;
    private final Map<Double, VelocityState> trendVelocityStateMap;
    private TrendDirection currentDirection = null;
    private VelocityState currentVelocityState = null;
    private final double accuracy;
    private final long timeDensityL;
    private final double timeDensityD;
    private final int historySize;
    private List<TrendListener> listeners = new ArrayList<TrendListener>();

    public AbstractTrendStrategy(double accuracy, long timeDensity) {
        this(accuracy, timeDensity, 16);
    }

    public AbstractTrendStrategy(double accuracy, double timeDensity) {
        this(accuracy, timeDensity, 16);
    }

    public AbstractTrendStrategy(double accuracy, long density, int historySize) {
        this.historySize = historySize;
        this.accuracy = accuracy;
        this.timeDensityL = density;
        this.timeDensityD = -1;
        trendPoints = new ArrayList<TrendPoint<Double, V>>(historySize);
        trendMap = new HashMap<Double, V>();
        trendDirectionMap = new HashMap<Double, TrendDirection>();
        trendVelocityStateMap = new HashMap<Double, VelocityState>();
    }

    public AbstractTrendStrategy(double accuracy, double density, int historySize) {
        this.historySize = historySize;
        this.accuracy = accuracy;
        this.timeDensityD = density;
        this.timeDensityL = -1;
        trendPoints = new ArrayList<TrendPoint<Double, V>>(historySize);
        trendMap = new HashMap<Double, V>();
        trendDirectionMap = new HashMap<Double, TrendDirection>();
        trendVelocityStateMap = new HashMap<Double, VelocityState>();
    }

    public final synchronized void acceptValue(Double point, V value) {
        if (accept(point, value)) {
            if (cycleIndex >= historySize) {
                cycleIndex = 0;
                isCyclic = true;
                TrendPoint<Double, V> trentPointToRemove = trendPoints.get(cycleIndex);
                dropTrendPoint(trentPointToRemove);
            }
            trendMap.put(point, value);
            TrendPoint tp = new TrendPoint<Double, V>(value, point);
            if (isCyclic)
                trendPoints.set(cycleIndex++, tp);
            else {
                trendPoints.add(tp);
                cycleIndex++;
            }
            int noOfTrendItems = trendPoints.size();
            if (noOfTrendItems > 1)
            applyTrendActions();
        }
    }

    private void dropTrendPoint(TrendPoint<Double, V> trendPointToRemove) {
        this.trendMap.remove(trendPointToRemove.point);
        this.trendDirectionMap.remove(trendPointToRemove.point);
        this.trendVelocityStateMap.remove(trendPointToRemove.point);
    }

    private final boolean accept(Double point, V value) {
        if (trendPoints.size() == 0)
            return true;
        if (trendMap.containsKey(point))
            return false;

        int thisTrendPoint = cycleIndex - 1;
        thisTrendPoint = thisTrendPoint < 0 ? trendPoints.size() + thisTrendPoint : thisTrendPoint;

        if (point.compareTo(trendPoints.get(thisTrendPoint).point) < 0)
            return false;

        double differentiation = Math.abs(point - trendPoints.get(thisTrendPoint).point);

        if (Math.abs(value.differantiate(trendPoints.get(thisTrendPoint).value)) < accuracy) {
            return false;
        }

        if (this.timeDensityD >= 0) {
            if (differentiation >= this.timeDensityD) {
                return true;
            }
        } else {
            if (differentiation >= this.timeDensityL) {
                return true;
            }
        }



        return false;
    }


    private final void applyTrendActions() {
        normalise();
        int size = trendPoints.size();
        if (size == 0)
            return;
        if (size == 1)
            return;

        int lastTrendPoint = cycleIndex - 2;
        int thisTrendPoint = cycleIndex - 1;
        if (lastTrendPoint < 0) {
            lastTrendPoint = trendPoints.size() + lastTrendPoint;
        }
        if (thisTrendPoint < 0) {
            thisTrendPoint = trendPoints.size() + thisTrendPoint;
        }

        double difference = trendPoints.get(thisTrendPoint).value.differantiate(trendPoints.get(lastTrendPoint).value);

        if (difference > accuracy) {
            currentDirection = TrendDirection.UP;
        } else if (difference == 0) {
            currentDirection = TrendDirection.NEUTRAL;
        } else if (difference <= -accuracy) {
            currentDirection = TrendDirection.DOWN;
        } else {
            currentDirection = TrendDirection.NEUTRAL;
        }
        trendDirectionMap.put(trendPoints.get(thisTrendPoint).point, currentDirection);

        computeAcceleration();
        onTrendUpdate();

    }

    protected void onTrendUpdate() {
        for (TrendListener listener : listeners) {
            if (shouldCallListener(listener)) {
                listener.onTrendEvent();
                if (listener instanceof StrategyVisitor) {
                    ((StrategyVisitor) listener).visit(this);
                }
            }
        }
    }

    private boolean shouldCallListener(TrendListener listener) {
        if (listener.getDirectionAction() != null && !listener.getDirectionAction().equals(currentDirection))
            return false;
        if (listener.getVelocityState() != null && !listener.getVelocityState().equals(currentVelocityState))
            return false;
        int previousPoint = cycleIndex - 1;
        if (previousPoint < 0)
            previousPoint = this.trendPoints.size() + previousPoint;
        V value = this.trendPoints.get(previousPoint).value;
        switch (listener.getDirectionAction()) {

            case UP:
                if (listener.getValueLimit().value.compareTo(value) < 0 )
                    return true;
                return false;
            case DOWN:
                if (listener.getValueLimit().value.compareTo(value) > 0 )
                    return true;
                return false;
            case NEUTRAL:
                return true;
            default:
                return false;
        }
    }

    public synchronized final List<TrendPoint<Double, V>> getTrendGraph(int startFrom, int endAt) {
        List<TrendPoint<Double, V>> trend = new ArrayList<TrendPoint<Double, V>>();
        if (startFrom < 0)
            startFrom = 0;
        if (endAt > trendPoints.size())
            endAt = trendPoints.size();
        if (isCyclic)
            return normaliseCyclicList(trend, startFrom, endAt);
        for (int i = startFrom; i < endAt; i++) {
            trend.add(trendPoints.get(i));
        }

        return trend;
    }

    private List<TrendPoint<Double,V>> normaliseCyclicList(List<TrendPoint<Double, V>> trend, int startFrom, int endAt) {
        startFrom = cycleIndex + startFrom;
        endAt = endAt - cycleIndex;
        while (startFrom != endAt) {
            if (startFrom >= trendPoints.size())
                startFrom = 0;
            trend.add(trendPoints.get(startFrom++));
        }
        return trend;
    }

    public synchronized List<TrendPoint<Double, V>> getTrendGraph(int latest) {
        int startFrom = historySize - latest;
        return getTrendGraph(startFrom, historySize);
    }

    public synchronized Map<Double, VelocityState> getVelocityGraph(int startFrom, int endAt) {
        Map<Double, VelocityState> velocityTrend = new HashMap<Double, VelocityState>();
        if (startFrom < 0)
            return velocityTrend;
        if (endAt > trendPoints.size())
            return velocityTrend;
        TrendPoint<Double, V> tp;
        List<TrendPoint<Double, V>> localTrendPoints = this.getTrendGraph(startFrom, endAt);
        for (int i = startFrom; i < endAt; i++) {
            tp = localTrendPoints.get(i);
            velocityTrend.put(tp.point, trendVelocityStateMap.get(tp.point));
        }
        return velocityTrend;
    }


    public synchronized Map<Double, TrendDirection> getDirectionGraph(int latest) {
        int startFrom = this.trendPoints.size() - latest;
        if (startFrom < 0)
            startFrom = 0;
        return getDirectionGraph(startFrom, historySize);
    }

    public synchronized Map<Double, TrendDirection> getDirectionGraph(int startFrom, int endAt) {
        Map<Double, TrendDirection> directionTrend = new HashMap<Double, TrendDirection>();
        if (startFrom < 0)
            return directionTrend;
        if (endAt > trendPoints.size())
            endAt = trendPoints.size();
        TrendPoint<Double, V> tp;
        List<TrendPoint<Double, V>> localTrendPoints = this.getTrendGraph(startFrom, endAt);
        for (int i = startFrom; i < endAt; i++) {
            tp = localTrendPoints.get(i);
            directionTrend.put(tp.point, this.trendDirectionMap.get(tp.point));
        }
        return directionTrend;
    }


    public synchronized Map<Double, VelocityState> getVelocityGraph(int latest) {
        int startFrom = historySize - latest;
        return getVelocityGraph(startFrom, historySize);
    }

    private final void computeAcceleration() {
        int size = trendPoints.size();
        if (size < 3)
            return;

        int pointBefore3Steps = cycleIndex - 3;
        int pointBefore2Steps = cycleIndex - 2;
        int pointBefore1Steps = cycleIndex - 1;
        pointBefore3Steps = (pointBefore3Steps < 0) ? size + pointBefore3Steps : pointBefore3Steps;
        pointBefore2Steps = (pointBefore2Steps < 0) ? size + pointBefore2Steps : pointBefore2Steps;
        pointBefore1Steps = (pointBefore1Steps < 0) ? size + pointBefore1Steps : pointBefore1Steps;

        double velocityFromAtoB = computeVelocity(trendPoints.get(pointBefore3Steps), trendPoints.get(pointBefore2Steps));
        double velocityFromBtoA = computeVelocity(trendPoints.get(pointBefore2Steps), trendPoints.get(pointBefore1Steps));
        double dt = trendPoints.get(pointBefore3Steps).value.differantiate(trendPoints.get(pointBefore1Steps).value);
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

        trendVelocityStateMap.put(trendPoints.get(pointBefore1Steps).point, currentVelocityState);

    }

    private final double computeVelocity(TrendPoint<Double, V> pointA, TrendPoint<Double, V> pointB) {
        return
                (pointA.value.differantiate(pointB.value)) /
                        (pointA.point - pointB.point);
    }

    private final void normalise() {
        applyFilters();
    }

    protected abstract void applyFilters();


    public int getSize() {
        return trendPoints.size();
    }

    public void registerEventListener(TrendListener trendListener) {
        listeners.add(trendListener);
    }
}
