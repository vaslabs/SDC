package com.vaslabs.sdc.logs;

import com.google.gson.Gson;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.VelocityEntry;
import com.vaslabs.sdc.ui.R;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vnicolao on 27/06/15.
 */
public final class LogbookStats {

    private float freeFallTime;
    private float maximumSpeed;
    private int maxUIndex;
    private float deploymentAltitude;
    private float exitAltitude;
    private int maxAltitudeIndex;
    private float maxAltitude;
    private long freeFallStartedTimestamp;
    private long freeFallEndedTimestamp;
    private VelocityEntry[] velocityEntries;
    private BarometerEntry[] averagedBarometerEntries;

    private LogbookStats() {

    }

    public static LogbookStats generateLogbookStats(BarometerEntries barometerEntries) {
        LogbookStats stats = new LogbookStats();
        stats.averagedBarometerEntries = average(barometerEntries, 3);
        stats.calculateMaxAltitude(barometerEntries);
        stats.calculateMaxSpeed(barometerEntries);
        stats.calculateDeployment();
        return stats;
    }

    private void calculateDeployment() {
        for (int i = maxUIndex+1; i < velocityEntries.length; i++) {
            if (maximumSpeed*0.6 > velocityEntries[i].velocity) {
                deploymentAltitude = averagedBarometerEntries[i].getAltitude();
                freeFallEndedTimestamp = averagedBarometerEntries[i].getTimestamp();
                freeFallTime = (freeFallEndedTimestamp - freeFallStartedTimestamp)/1000f;
                return;
            }
        }
    }

    private void calculateMaxSpeed(BarometerEntries barometerEntries) {
        velocityEntries = new VelocityEntry[averagedBarometerEntries.length];
        velocityEntries[0] = new VelocityEntry(barometerEntries.get(0).getTimestamp(), 0f);
        maximumSpeed = 0;
        maxUIndex = 0;
        float dt, dx;
        for (int i = 1; i < averagedBarometerEntries.length; i++)  {
            dx = averagedBarometerEntries[i].getAltitude() - averagedBarometerEntries[i-1].getAltitude();
            dt = (averagedBarometerEntries[i].getTimestamp() - averagedBarometerEntries[i-1].getTimestamp())/1000f;
            velocityEntries[i] = new VelocityEntry(averagedBarometerEntries[i].getTimestamp(), dx/dt);
            if (velocityEntries[i].velocity > maximumSpeed) {
                maximumSpeed = velocityEntries[i].velocity;
                maxUIndex = i;
            }
        }

    }

    public static VelocityEntry[] calculateVelocityValues(BarometerEntry[] barometerEntries) {
        VelocityEntry[] velocityEntries = new VelocityEntry[barometerEntries.length];
        velocityEntries[0] = new VelocityEntry(barometerEntries[0].getTimestamp(), 0f);
        float dt, dx;
        for (int i = 1; i < barometerEntries.length; i++)  {
            dx = barometerEntries[i].getAltitude() - barometerEntries[i-1].getAltitude();
            dt = (barometerEntries[i].getTimestamp() - barometerEntries[i-1].getTimestamp())/1000f;
            velocityEntries[i] = new VelocityEntry(barometerEntries[i].getTimestamp(), dx/dt);

        }
        return velocityEntries;
    }

    public static BarometerEntry[] average(BarometerEntries barometerEntries, int density) {
        BarometerEntry[] avgEntries = new BarometerEntry[barometerEntries.size()/density];
        long timestampAvg = 0;
        float altitudeAvg = 0;
        int counter = 0;
        for (int i = 0; i < barometerEntries.size() - density; i+=density) {

            for (int j=0; j < density; j++) {
                altitudeAvg += (barometerEntries.get(i+j).getAltitude()/density);
                timestampAvg += (barometerEntries.get(i+j).getTimestamp()/density);
            }
            avgEntries[counter++] = new BarometerEntry(timestampAvg, altitudeAvg);
            timestampAvg = 0;
            altitudeAvg = 0;
        }

        return avgEntries;
    }

    private void calculateMaxAltitude(BarometerEntries barometerEntries) {
        maxAltitude = averagedBarometerEntries[0].getAltitude();
        maxAltitudeIndex = 0;
        for (int i = 0; i < averagedBarometerEntries.length; i++) {
            if ( averagedBarometerEntries[i].getAltitude() > maxAltitude) {
                maxAltitude = averagedBarometerEntries[i].getAltitude();
                maxAltitudeIndex = i;
            }
        }
        for (int i = maxAltitudeIndex + 1; i < averagedBarometerEntries.length; i++) {
            if (maxAltitude - averagedBarometerEntries[i].getAltitude() > 10) {
                exitAltitude = averagedBarometerEntries[i-1].getAltitude();
                freeFallStartedTimestamp = averagedBarometerEntries[i-1].getTimestamp();
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "LogbookStats{" +
                "freeFallTime=" + freeFallTime +
                ", maximumSpeed=" + maximumSpeed +
                ", deploymentAltitude=" + deploymentAltitude +
                ", exitAltitude=" + exitAltitude +
                ", maxAltitude=" + maxAltitude +
                '}';
    }

    public float getFreeFallTime() {
        return freeFallTime;
    }

    public float getMaximumSpeed() {
        return maximumSpeed;
    }

    public float getDeploymentAltitude() {
        return deploymentAltitude;
    }

    public float getExitAltitude() {
        return exitAltitude;
    }

}
