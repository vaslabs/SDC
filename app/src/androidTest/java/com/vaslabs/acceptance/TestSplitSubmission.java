package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.structs.DateStruct;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by vnicolaou on 11/11/15.
 */
public class TestSplitSubmission extends AndroidTestCase {

    public void test_that_sessions_are_not_splitted() throws Exception {
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.sample_split_log));
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(isr, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        assertEquals(1, sessionDates.size());
        SDCLogManager logManager = SDCLogManager.getInstance(this.getContext());
        Map<DateStruct, SkydivingSessionData> successfullySubmittedSessions = logManager.submitLogs(sessionDates);
        sessionData = SessionFilter.mostRecent(successfullySubmittedSessions);

        SDCLogManager.getInstance(this.getContext()).saveLatestSession(sessionData);
    }


}
