package com.vaslabs.acceptance;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.math.SDCMathUtils;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.structs.DateStruct;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by vnicolaou on 25/10/15.
 */
public class TestSessionFilter extends AndroidTestCase {

    public void test_that_number_of_sessions_is_detected_correctly() throws Exception {
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.test_multiple_sessions));
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(isr, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        assertEquals(2, sessionDates.size());

        float min = SDCMathUtils.findMin(sessionData.getBarometerEntries());
        assertEquals(0.0, min, 5);
        Method numberOfSessionsMethod = SessionFilter.class.getDeclaredMethod("detectNumberOfSessions", BarometerEntries.class, Float.TYPE);
        numberOfSessionsMethod.setAccessible(true);
        int result = (int) numberOfSessionsMethod.invoke(null, sessionData.getBarometerEntries(), 0);
        assertEquals(2, result);
    }

    public void test_that_number_of_timestamps_for_sessions_is_detected_correctly() throws Exception {
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.test_multiple_sessions));
        Gson gson = new Gson();
        SkydivingSessionData sessionData = gson.fromJson(isr, SkydivingSessionData.class);
        Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
        assertEquals(2, sessionDates.size());

        float min = SDCMathUtils.findMin(sessionData.getBarometerEntries());
        assertEquals(0.0, min, 5);
        Method numberOfSessionsMethod = SessionFilter.class.getDeclaredMethod("detectNumberOfSessions", BarometerEntries.class, Float.TYPE);
        numberOfSessionsMethod.setAccessible(true);
        int result = (int) numberOfSessionsMethod.invoke(null, sessionData.getBarometerEntries(), 0);
        assertEquals(2, result);
        Method timestampsMethod = SessionFilter.class.getDeclaredMethod("detectSessionsTimestamps", BarometerEntries.class, int.class, float.class);
        timestampsMethod.setAccessible(true);
        List<Long> timestamps = (List<Long>) timestampsMethod.invoke(null, sessionData.getBarometerEntries(), result, min);
        assertEquals(3, timestamps.size());

        Method splitToSingleSessionsMethod = SessionFilter.class.getDeclaredMethod("splitToSingleSessions", SkydivingSessionData.class);
        splitToSingleSessionsMethod.setAccessible(true);
        SkydivingSessionData[] ssd = (SkydivingSessionData[]) splitToSingleSessionsMethod.invoke(null, sessionData);
        assertEquals(2, ssd.length);
    }



}
