package com.vaslabs.sdc.logs;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.cache.CacheManager;
import com.vaslabs.sdc.entries.AccelerationEntry;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GForceEntry;
import com.vaslabs.sdc.entries.VelocityEntry;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.sdc.types.SkydivingEvent;
import com.vaslabs.sdc.types.SkydivingEventDetails;
import com.vaslabs.sdc.ui.Main2Activity;
import com.vaslabs.sdc.ui.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vnicolao on 27/06/15.
 */
public final class LogbookStats {

    private float maximumSpeed;
    private float deploymentAltitude;
    private float exitAltitude;
    private long freeFallStartedTimestamp;
    private long freeFallEndedTimestamp;

    private LogbookStats() {

    }

    //return new SkydivingEventDetails[]{takeOffEventDetail, freeFallDetail, canopyEventDetail, landedEventDetail};

    public static LogbookStats generateLogbookStats(BarometerEntries barometerEntries) {
        if (barometerEntries.size() < 200)
            return null;
        LogbookStats stats = new LogbookStats();
        SkydivingEventDetails[] flyingEvents = identifyFlyingEvents(barometerEntries);

        SkydivingEventDetails freeFallDetail = flyingEvents[1];
        stats.freeFallStartedTimestamp = freeFallDetail.timestamp;
        stats.freeFallEndedTimestamp = flyingEvents[2].timestamp;
        BarometerEntry[] avgBarometerEntries = LogbookStats.average(barometerEntries, 1000);
        VelocityEntry[] velocityEntries = LogbookStats.calculateVelocityValues(avgBarometerEntries, 8000);
        VelocityEntry maxVelocity = findMaxNegativeVelocity(velocityEntries);
        stats.maximumSpeed = maxVelocity.velocity;

        stats.deploymentAltitude = avgBarometerEntries[findBarometerEntry(avgBarometerEntries, stats.freeFallEndedTimestamp)].getAltitude();
        stats.exitAltitude = avgBarometerEntries[findBarometerEntry(avgBarometerEntries, stats.freeFallStartedTimestamp)].getAltitude();

        return stats;
    }

    private static VelocityEntry findMaxNegativeVelocity(VelocityEntry[] velocityEntries) {
        VelocityEntry maxNegative = velocityEntries[0];
        for (VelocityEntry ve : velocityEntries) {
            if (ve.velocity < maxNegative.velocity) {
                maxNegative = ve;
            }
        }
        return maxNegative;
    }

    public static SkydivingSessionData getLatestSession() {
        return CacheManager.getLastSession();
    }


    public static VelocityEntry[] calculateVelocityValues(BarometerEntry[] barometerEntries, int density) {
        List<VelocityEntry> velocityEntries = new ArrayList<VelocityEntry>(barometerEntries.length);
        velocityEntries.add(new VelocityEntry(barometerEntries[0].getTimestamp(), 0f));
        float x1, x2;
        x1 = barometerEntries[0].getAltitude();
        long startTimestamp = barometerEntries[0].getTimestamp();
        long endTimestamp = startTimestamp + density;
        for (int i = 1; i < barometerEntries.length; i++)  {
            if (barometerEntries[i].getTimestamp() > endTimestamp) {
                x2 = barometerEntries[i].getAltitude();
                final long avgTimestamp = (barometerEntries[i].getTimestamp()/2 + startTimestamp/2);
                final float avgSpeed = (x2-x1)/((barometerEntries[i].getTimestamp() - startTimestamp)/1000f);
                velocityEntries.add(new VelocityEntry(avgTimestamp, avgSpeed));
                startTimestamp = barometerEntries[i].getTimestamp();
                endTimestamp = startTimestamp + density;
                x1 = x2;
            }
        }
        VelocityEntry[] velocityEntriesArray = new VelocityEntry[velocityEntries.size()];
        return velocityEntries.toArray(velocityEntriesArray);
    }

    public static BarometerEntry[] average(BarometerEntries barometerEntries, int density) {
        List<BarometerEntry> avgEntries = new ArrayList<BarometerEntry>();
        List<Float> buffer = new ArrayList<Float>();
        BarometerEntry barometerEntry = barometerEntries.get(0);
        buffer.add(barometerEntry.getAltitude());
        long bufferTimestamp = barometerEntry.getTimestamp();
        long endTimestamp = barometerEntry.getTimestamp() + density;
        for (int i = 0; i < barometerEntries.size(); i++) {
            barometerEntry = barometerEntries.get(i);
            if (barometerEntry.getTimestamp() <= endTimestamp) {
                buffer.add(barometerEntry.getAltitude());
            } else {
                final float altitudeSum = SDCMathUtils.sumBuffer(buffer);
                avgEntries.add(new BarometerEntry((bufferTimestamp/2 + endTimestamp/2), altitudeSum/buffer.size()));
                buffer.clear();
                buffer.add(barometerEntry.getAltitude());
                bufferTimestamp = barometerEntry.getTimestamp();
                endTimestamp = bufferTimestamp + density;
            }
        }
        BarometerEntry[] barometerEntriesArray = new BarometerEntry[avgEntries.size()];
        return avgEntries.toArray(barometerEntriesArray);
    }

    @Override
    public String toString() {
        return "LogbookStats{" +
                ", maximumSpeed=" + maximumSpeed +
                ", deploymentAltitude=" + deploymentAltitude +
                ", exitAltitude=" + exitAltitude +
                '}';
    }

    public float getFreeFallTime() {
        return (freeFallEndedTimestamp - freeFallStartedTimestamp)/1000f;
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

    public static AccelerationEntry[] calculateAccelerationValues(VelocityEntry[] velocityEntries) {
        if (velocityEntries.length == 0) {
            return new AccelerationEntry[0];
        }
        AccelerationEntry[] accelerationEntries = new AccelerationEntry[velocityEntries.length];
        accelerationEntries[0] = new AccelerationEntry(velocityEntries[0].getTimestamp(), velocityEntries[0].velocity);
        float du, dt;
        for (int i = 1; i < velocityEntries.length; i++) {
            du = velocityEntries[i].velocity - velocityEntries[i-1].velocity;
            dt = (velocityEntries[i].getTimestamp() - velocityEntries[i-1].getTimestamp())/1000.0f;
            accelerationEntries[i] = new AccelerationEntry(velocityEntries[i].getTimestamp(), du/dt);
        }
        return accelerationEntries;
    }

    public static float maxY(Entry[] entries) {
        float maxY = entries[0].getY();
        for (int i = 1; i < entries.length; i++) {
            if (entries[i].getY() > maxY) {
                maxY = entries[i].getY();
            }
        }
        return maxY;
    }

    public static float minY(Entry[] entries) {
        float minY = entries[0].getY();
        for (int i = 1; i < entries.length; i++) {
            if (entries[i].getY() < minY) {
                minY = entries[i].getY();
            }
        }
        return minY;
    }

    public static GForceEntry[] calculateGForce(Entry[] entries) {
        if (entries.length == 0)
            return new GForceEntry[0];
        GForceEntry[] gForceEntries = new GForceEntry[entries.length];
        int counter = 0;
        for (Entry entry : entries) {
            gForceEntries[counter++] = new GForceEntry(entry.getTimestamp(), entry.getY()/9.8f);
        }
        return gForceEntries;
    }

    public static SkydivingEventDetails[] identifyFlyingEvents(BarometerEntries barometerEntries) {
        barometerEntries.sort();
        BarometerEntry[] avgBarometerEntries = LogbookStats.average(barometerEntries, 1000);
        Arrays.sort(avgBarometerEntries);
        VelocityEntry[] velocityEntries = LogbookStats.calculateVelocityValues(avgBarometerEntries, 8000);
        AccelerationEntry[] accelerationEntries = LogbookStats.calculateAccelerationValues(velocityEntries);
        AccelerationEntry maxPositiveAcceleration = findMaxPositiveAcceleration(accelerationEntries);
        AccelerationEntry maxNegativeAcceleration = findMaxNegativeAcceleration(accelerationEntries);
        SkydivingEventDetails canopyEventDetail = new SkydivingEventDetails(SkydivingEvent.CANOPY, maxPositiveAcceleration.getTimestamp());
        SkydivingEventDetails freeFallDetail = new SkydivingEventDetails(SkydivingEvent.FREE_FALL, maxNegativeAcceleration.getTimestamp());
        SkydivingEventDetails landedEventDetail = getLandedEvent(avgBarometerEntries, maxNegativeAcceleration);
        SkydivingEventDetails takeOffEventDetail = getTakeOffEvent(avgBarometerEntries);
        return new SkydivingEventDetails[]{takeOffEventDetail, freeFallDetail, canopyEventDetail, landedEventDetail};
    }

    private static SkydivingEventDetails getTakeOffEvent(BarometerEntry[] barometerEntries) {
        int maxBarometerEntryIndex = findMax(barometerEntries);
        for (int i = maxBarometerEntryIndex - 1; i >= 0; i--) {
            if (Math.abs(barometerEntries[i].getAltitude() - barometerEntries[0].getAltitude()) < 5f)
                return new SkydivingEventDetails(SkydivingEvent.TAKE_OFF, barometerEntries[i].getTimestamp());
        }
        return new SkydivingEventDetails(SkydivingEvent.TAKE_OFF, barometerEntries[maxBarometerEntryIndex - ((barometerEntries.length - maxBarometerEntryIndex)/2)].getTimestamp());
    }

    private static int findMax(Entry[] avgBarometerEntries) {
        int maxIndex = 0;
        Entry maxEntry = avgBarometerEntries[0];
        for (int i = 1; i < avgBarometerEntries.length; i++) {
            if (avgBarometerEntries[i].getY() > maxEntry.getY()) {
                maxIndex = i;
                maxEntry = avgBarometerEntries[i];
            }
        }
        return maxIndex;
    }

    public static SkydivingEventDetails getLandedEvent(BarometerEntry[] barometerEntries, AccelerationEntry maxNegativeAcceleration) {
        BarometerEntry minBarometerEntryBeforeTakeOff = findMinBarometerEntryBefore(barometerEntries, maxNegativeAcceleration.getTimestamp());
        int barometerEntryIndex = findBarometerEntry(barometerEntries, maxNegativeAcceleration.getTimestamp());
        for (int i = barometerEntryIndex + 1; i < barometerEntries.length; i++) {
            if (barometerEntries[i].getAltitude() - 1 < minBarometerEntryBeforeTakeOff.getAltitude())
                return new SkydivingEventDetails(SkydivingEvent.LANDING, barometerEntries[barometerEntryIndex].getTimestamp());
        }
        return new SkydivingEventDetails(SkydivingEvent.LANDING, barometerEntries[barometerEntryIndex + ((barometerEntries.length - barometerEntryIndex)/2)].getTimestamp());
    }

    private static BarometerEntry findMinBarometerEntryBefore(BarometerEntry[] barometerEntries, long timestamp) {
        int barometerEntryIndex = findBarometerEntry(barometerEntries, timestamp);
        BarometerEntry be = barometerEntries[barometerEntryIndex];
        for (int i = barometerEntryIndex - 1; i >= 0; i--) {
            if (barometerEntries[i].getAltitude() < be.getAltitude()) {
                be = barometerEntries[i];
            }
        }
        return be;
    }

    public static int findBarometerEntry(BarometerEntry[] barometerEntries, long timestamp) {
        int leftIndex = 0;
        int rightIndex = barometerEntries.length - 1;
        int midIndex = 0;
        long tmpTimestamp;
        while (leftIndex <= rightIndex) {
            midIndex = leftIndex + (rightIndex - leftIndex)/2;
            tmpTimestamp = barometerEntries[midIndex].getTimestamp();
            if (tmpTimestamp == timestamp) {
                return midIndex;
            } else if (tmpTimestamp < timestamp) {
                leftIndex = midIndex+1;
            } else {
                rightIndex = midIndex - 1;
            }
        }
        return midIndex;
    }

    private static AccelerationEntry findMaxPositiveAcceleration(AccelerationEntry[] accelerationEntries) {
        AccelerationEntry maxPositive = accelerationEntries[0];
        for (AccelerationEntry ae : accelerationEntries) {
            if (ae.acceleration > maxPositive.acceleration) {
                maxPositive = ae;
            }
        }
        return maxPositive;
    }

    private static AccelerationEntry findMaxNegativeAcceleration(AccelerationEntry[] accelerationEntries) {
        AccelerationEntry maxNegative = accelerationEntries[0];
        for (AccelerationEntry ae : accelerationEntries) {
            if (ae.acceleration < maxNegative.acceleration) {
                maxNegative = ae;
            }
        }
        return maxNegative;
    }

    public static LogbookStats[] generateLogbookStats(SkydivingSessionData[] skydivingSessionDatas) {
        if (skydivingSessionDatas == null)
            return null;
        List<LogbookStats> logbookStats = new LinkedList<>();
        for (int i = 0; i < skydivingSessionDatas.length; i++) {
            LogbookStats ls = generateLogbookStats(skydivingSessionDatas[i].getBarometerEntries());
            if (isValid(ls))
                logbookStats.add(ls);
        }
        LogbookStats[] logbookStatsArray = new LogbookStats[logbookStats.size()];
        return logbookStats.toArray(logbookStatsArray);
    }

    private static boolean isValid(LogbookStats ls) {
        return ls != null && ls.getFreeFallTime() != 0 && ls.getDeploymentAltitude() != 0 && ls.getMaximumSpeed() != 0 && (ls.exitAltitude > ls.deploymentAltitude);
    }

    public long getTimeInMillis() {
        return freeFallStartedTimestamp;
    }
}
