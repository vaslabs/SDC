package com.vaslabs.logbook;

/**
 * Created by vnicolaou on 31/08/15.
 */
/*
"deploymentAltitude": 0,
 "freefalltime": 0,
 "maxVelocity": 0,
 "exitAltitude": 0
 */
public final class LogbookMetrics {
    private float deploymentAltitude;
    private float freefalltime;
    private float maxVelocity;
    private float exitAltitude;

    public float getDeploymentAltitude() {
        return deploymentAltitude;
    }

    public float getFreefalltime() {
        return freefalltime;
    }

    public float getMaxVelocity() {
        return maxVelocity;
    }

    public float getExitAltitude() {
        return exitAltitude;
    }
}
