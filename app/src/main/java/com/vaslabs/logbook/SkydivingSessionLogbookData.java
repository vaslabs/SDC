package com.vaslabs.logbook;

import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolao on 03/07/15.
 */
public class SkydivingSessionLogbookData {

    private BarometerEntry[] barometerEntries;

    private SkydivingSessionLogbookData() {

    }



    public static SkydivingSessionLogbookData create(BarometerEntries barometerEntries) {
        SkydivingSessionLogbookData logbook = new SkydivingSessionLogbookData();
        BarometerEntry[] avgBarometerEntries = LogbookStats.average(barometerEntries, 1000);
        int takeOffIndex = findTakeOffIndex(avgBarometerEntries);

        int landedIndex = findLandingIndex(avgBarometerEntries);

        logbook.barometerEntries = new BarometerEntry[landedIndex + 1 - takeOffIndex];

        for (int i = takeOffIndex; i <= landedIndex; i++) {
            logbook.barometerEntries[i-takeOffIndex] = avgBarometerEntries[i];
        }
        return logbook;
    }

    private static int findLandingIndex(BarometerEntry[] barometerEntries) {
        int currentIndex = 0;
        return findLandingIndex(barometerEntries, barometerEntries.length - 10, false);
    }

    private static int findLandingIndex(BarometerEntry[] barometerEntries, int i, boolean canopy) {
        if (!canopy) {
            if (barometerEntries[i].getAltitude() > barometerEntries[barometerEntries.length - 1].getAltitude() + 30) {
                return findLandingIndex(barometerEntries, i, true);
            } else {
                return findLandingIndex(barometerEntries, i-10, false);
            }
        } else {
            if (barometerEntries[i].getAltitude() <= barometerEntries[i].getAltitude() + 10) {
                return i;
            } else {
                return findLandingIndex(barometerEntries, i + 1, true);
            }
        }
    }

    private static int findTakeOffIndex(BarometerEntry[] avgBarometerEntries) {
        return findTakeOffIndex(avgBarometerEntries, 100, false);
    }

    private static int findTakeOffIndex(BarometerEntry[] avgBarometerEntries, int i, boolean liftOf) {
        if (!liftOf) {
            if (avgBarometerEntries[i].getAltitude() > avgBarometerEntries[0].getAltitude() + 50) {
                return findTakeOffIndex(avgBarometerEntries, i, true);
            } else
                return findTakeOffIndex(avgBarometerEntries, i + 100, false);
        } else {
            if (avgBarometerEntries[i].getAltitude() <= avgBarometerEntries[0].getAltitude() + 10) {
                return i;
            }
            else {
                return findTakeOffIndex(avgBarometerEntries, i-10, true);
            }
        }
    }

    public BarometerEntry getEntryAt(int i) {
        return barometerEntries[i];
    }
}
