package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolaou on 01/11/15.
 */
public class TestLogbookStats extends AndroidTestCase {

    public void test_findBarometerEntry() {
        BarometerEntry[] barometerEntries = new BarometerEntry[]{new BarometerEntry(2, 8f),
        new BarometerEntry(3, 7f), new BarometerEntry(6,8f), new BarometerEntry(8, 10f)};
        assertEquals(0, LogbookStats.findBarometerEntry(barometerEntries, 2));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 6));
        assertEquals(3, LogbookStats.findBarometerEntry(barometerEntries, 7));

        barometerEntries = new BarometerEntry[]{new BarometerEntry(2, 8f),
                new BarometerEntry(3, 7f), new BarometerEntry(6,8f) };
        assertEquals(0, LogbookStats.findBarometerEntry(barometerEntries, 2));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 6));
        assertEquals(2, LogbookStats.findBarometerEntry(barometerEntries, 7));
    }
}
