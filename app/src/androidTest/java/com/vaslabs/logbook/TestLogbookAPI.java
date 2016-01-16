package com.vaslabs.logbook;

import android.test.AndroidTestCase;


import java.util.List;

/**
 * Created by vnicolaou on 15/08/15.
 */
public class TestLogbookAPI extends AndroidTestCase{

    LogbookAPI logbookAPI = null;

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().toString());
        logbookAPI = LogbookAPI.INSTANCE;
    }

    public void test_connectivity() throws Exception {
    }

    public void test_logbook_details_object_mock_data() throws Exception {
        List<Logbook> logbookList = logbookAPI.getLogbookEntries();
        assertEquals(2, logbookList.size());
    }

    public void test_logbook_summary_from_entries() throws Exception {
        List<Logbook> logbookList = logbookAPI.getLogbookEntries();
        LogbookSummary ls = LogbookSummary.fromLogbookEntries(logbookList);
        assertEquals(668.26f, ls.getAverageDeployAltitude());
        assertEquals(-85.18f, ls.getAverageTopSpeed());
        assertEquals(3415.65f, ls.getAverageExitAltitude());
        assertEquals(1434870982000L, ls.getLatestJumpDate());
        assertEquals(2, ls.getNumberOfJumps());
    }


}
