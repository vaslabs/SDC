package com.vaslabs.logbook;

import com.vaslabs.sdc.logs.LogbookStats;

import java.util.Date;
import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public final class LogbookSummary {

    private float averageTopSpeed = 0;
    private int numberOfJumps = 0;
    private long latestJumpDate = 0;
    private float averageExitAltitude = 0;
    private float averageDeployAltitude = 0;
    private float averageSpeed = 0;

    public int getNumberOfJumps() {
        return numberOfJumps;
    }

    public long getLatestJumpDate() {
        return latestJumpDate;
    }

    public float getAverageExitAltitude() {
        return averageExitAltitude;
    }

    public float getAverageDeployAltitude() {
        return averageDeployAltitude;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public float getAverageTopSpeed() {
        return averageTopSpeed;
    }

    public static LogbookSummary fromLogbookEntries(LogbookStats[] logbookStats) {
        LogbookSummary ls = new LogbookSummary();
        int countTopSpeed=0, countAverageExitAltitude=0, countAverageDeployAltitude=0, countAverageSpeed=0;
        for (LogbookStats l : logbookStats) {
            if (ls.latestJumpDate < l.getTimeInMillis())
                ls.latestJumpDate = l.getTimeInMillis();
            if (l.getDeploymentAltitude() != 0) {
                ls.averageDeployAltitude += l.getDeploymentAltitude();
                countAverageDeployAltitude++;
            }
            if (l.getMaximumSpeed() != 0) {
                ls.averageTopSpeed += l.getMaximumSpeed();
                countTopSpeed++;
            }
            if (l.getExitAltitude() != 0) {
                ls.averageExitAltitude += l.getExitAltitude();
                countAverageExitAltitude++;
            }
        }
        if (countAverageDeployAltitude != 0)
            ls.averageDeployAltitude /= countAverageDeployAltitude;
        if (countAverageExitAltitude != 0) {
            ls.averageTopSpeed /= countAverageExitAltitude;
        }
        if (countAverageExitAltitude != 0) {
            ls.averageExitAltitude /= countAverageExitAltitude;
        }

        ls.numberOfJumps = logbookStats.length;

        return ls;
    }
}
