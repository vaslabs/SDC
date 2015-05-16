package com.vaslabs.sdc.utils;

import com.vaslabs.sdc.types.DifferentiableFloat;
import com.vaslabs.sdc.types.TrendPoint;

/**
 * Created by vnicolao on 16/05/15.
 */
public abstract class DefaultBarometerTrendListener implements TrendListener{

    private TrendDirection directionAction;


    private TrendPoint<Double, DifferentiableFloat> altitudeLimit;


    private VelocityState velocityState;

    public void forCertainAltitude(TrendPoint<Double, DifferentiableFloat> altitude) {
        altitudeLimit = altitude;
    }

    public void forDirectionAction(TrendDirection td) {
        directionAction = td;
    }

    public void forAccelerationAction(VelocityState vs) {
        velocityState = vs;
    }

    @Override
    public VelocityState getVelocityState() {
        return velocityState;
    }

    @Override
    public TrendPoint<Double, DifferentiableFloat> getValueLimit() {
        return altitudeLimit;
    }

    @Override
    public TrendDirection getDirectionAction() {
        return directionAction;
    }


}
