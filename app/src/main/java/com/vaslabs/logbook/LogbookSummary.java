package com.vaslabs.logbook;

import java.util.Date;

/**
 * Created by vnicolaou on 15/08/15.
 */
public final class LogbookSummary {

    private double averageTopSpeed;
    private int numberOfJumps;
    private long latestJumpDate;
    private int averageExitAltitude;
    private int averageDeployAltitude;
    private double averageSpeed;

    public int getNumberOfJumps() {
        return numberOfJumps;
    }

    public long getLatestJumpDate() {
        return latestJumpDate;
    }

    public int getAverageExitAltitude() {
        return averageExitAltitude;
    }

    public int getAverageDeployAltitude() {
        return averageDeployAltitude;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getAverageTopSpeed() {
        return averageTopSpeed;
    }
}
