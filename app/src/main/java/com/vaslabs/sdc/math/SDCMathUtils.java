package com.vaslabs.sdc.math;

import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.BarometerEntries;

import java.util.List;

/**
 * Created by vnicolaou on 24/10/15.
 */
public class SDCMathUtils {
    public static float sumBuffer(List<Float> buffer) {
        float sum = 0;
        for (float value : buffer) {
            sum += value;
        }
        return sum;
    }


    public static float findMin(BarometerEntries barometerEntries) {
        barometerEntries.sort();
        if (barometerEntries == null || barometerEntries.size() == 0)
            return 0;
        float min = barometerEntries.get(0).getAltitude();
        for (int i = 1; i < barometerEntries.size(); i++) {
            if (barometerEntries.get(i).getAltitude() < min) {
                min = barometerEntries.get(i).getAltitude();
            }
            if (barometerEntries.get(i).getAltitude() > 1000)
                break;
        }
        return min;
    }

    public static long findMaxTimestamp(SkydivingSessionData skydivingSessionData) {
        long[] timestamps = new long[3];
        skydivingSessionData.getBarometerEntries().sort();
        skydivingSessionData.getConnectionEntries().sort();
        skydivingSessionData.getGpsEntries().sort();
        int size = skydivingSessionData.getBarometerEntries().size();
        if (size > 0)
            timestamps[0] = skydivingSessionData.getBarometerEntries().get(size - 1).getTimestamp();
        size = skydivingSessionData.getGpsEntries().size();
        if (size > 0)
            skydivingSessionData.getGpsEntries().getEntry( size - 1);
        size = skydivingSessionData.getConnectionEntries().size();
        if (size > 0) {
            skydivingSessionData.getConnectionEntries().getEntry( size - 1);
        }
        return Math.max(Math.max(timestamps[0], timestamps[1]), timestamps[2]);
    }
}
