package com.vaslabs.emergency;

import android.test.AndroidTestCase;

import com.vaslabs.units.TimeUnit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class TestEmergencyCallAlgorithm extends AndroidTestCase {

    @Override
    public void setUp() {
    }

    public void test_emergency_preferences() {
        EmergencyPreferences ep = EmergencyPreferences.load(mContext);
        double minutes = ep.getMinimumTimeBeforeCall(TimeUnit.MINUTES);
        assertTrue(minutes >= 5);
        List<EmergencyContact> emergencyContactList = ep.getEmergencyContactList();
        assertNotNull(emergencyContactList);
    }

    public void test_save_emergency_preferences() {
        EmergencyPreferences ep = EmergencyPreferences.load(mContext);
        try {
            ep.save();
            String jsonData = readAll(mContext.openFileInput("emergencypreferences.json"));
            assertTrue(jsonData.contains("minimumTimeBeforeCall"));
        } catch (Exception e) {
            fail(e.toString());
        }
        try {
            ep.addEmergencyContact("Name", "PhoneNumber");
        } catch (Exception e) {
            fail(e.toString());
        }
        try {
            ep.setMinimumTimeBeforeEmergencyCall(10.0, TimeUnit.MINUTES);
        } catch (Exception e) {
            fail(e.toString());
        }
        ep = EmergencyPreferences.load(mContext);
        assertEquals(10.0, ep.getMinimumTimeBeforeCall(TimeUnit.MINUTES), 0.00001);
        List<EmergencyContact> emergencyContactList = ep.getEmergencyContactList();
        assertEquals("Name", emergencyContactList.get(0).name);
        assertEquals("PhoneNumber", emergencyContactList.get(0).phoneNumber);
    }

    private String readAll(FileInputStream fileInputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(fileInputStream);
        BufferedReader br = new BufferedReader(isr);
        try {String line;
            String content = "";
            while ((line= br.readLine()) != null)
                content += line;
            return content;
        } finally {
            br.close();
        }

    }

    public void test_detection_of_non_moving_skydiver() {

    }
}
