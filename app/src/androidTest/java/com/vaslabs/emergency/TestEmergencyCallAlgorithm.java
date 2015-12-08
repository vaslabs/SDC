package com.vaslabs.emergency;

import android.test.AndroidTestCase;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.logs.LatLng;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.units.TimeUnit;
import com.vaslabs.vtrends.impl.AbstractTrendStrategy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


import android.provider.ContactsContract;

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

    public void test_send_sms() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SkyDivingEnvironment.getInstance(this.mContext);
        EmergencyPositionalTrendListener trendListener = new EmergencyPositionalTrendListener(0, TimeUnit.MINUTES, this.mContext);
        Method sendSmsMethod = EmergencyPositionalTrendListener.class.getDeclaredMethod("sendEmergencySms");
        sendSmsMethod.setAccessible(true);
        sendSmsMethod.invoke(trendListener, null);
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

    public void test_detection_of_non_moving_skydiver() throws NoSuchFieldException, IllegalAccessException {

        List<GpsEntry> positionDetails = onGenerateSomeGpsMockData();
        List<BarometerEntry> barometerEntries = onGenerateSomeBarometerMockData();
        if (barometerEntries.size() == 0)
            barometerEntries.add(new BarometerEntry(System.currentTimeMillis(), 89.02f));
        List<Entry> allEntries = merge(positionDetails, barometerEntries);

        assertEquals(5*60*1000, allEntries.get(allEntries.size() - 1).getTimestamp() - allEntries.get(0).getTimestamp());
        final AbstractTrendStrategy<Position> trendStrategy = new PositionalTrendStrategy<Position>(0.0, 30.0, 50);
        EmergencyPositionalTrendListener trendListener = new EmergencyPositionalTrendListener(5.0, TimeUnit.MINUTES, this.mContext);
        trendStrategy.registerEventListener(trendListener);

        GpsEntry lastKnownLocation = positionDetails.get(0);

        BarometerEntry lastKnownAltitude = barometerEntries.get(0);
        Position p;
        for (Entry e : allEntries) {
            if (e instanceof BarometerEntry) {
                lastKnownAltitude = (BarometerEntry) e;
            } else if (e instanceof GpsEntry) {
                lastKnownLocation = (GpsEntry)e;
            }
            LatitudeSensorValue latSV = new LatitudeSensorValue(lastKnownLocation.getLatitude());
            LongitudeSensorValue lonSV = new LongitudeSensorValue(lastKnownLocation.getLongitude());
            MetersSensorValue msv = new MetersSensorValue(lastKnownAltitude.getAltitude());
            p = new Position(lonSV, latSV, msv);
            trendStrategy.acceptValue(e.getTimestamp()/1000.0, p);
        }

        Field timesCalledField = EmergencyPositionalTrendListener.class.getDeclaredField("timesCalled");
        timesCalledField.setAccessible(true);
        int value = timesCalledField.getInt(trendListener);
        assertTrue(value > 0);

    }

    private List<Entry> replicateData(List<Entry> allEntries, int minimumSize) {
        if (minimumSize <= allEntries.size())
            return allEntries;
        int startFrom = 0;
        List<Entry> entries = new ArrayList<Entry>((int) (minimumSize + Math.round(Math.random() * 1000)));
        entries.addAll(allEntries);
        int iStart = entries.size();
        for (int i = iStart; i <= minimumSize; i++) {
            Entry entryToReplicate = entries.get(startFrom++);
            long lastTimeStamp = entries.get(entries.size() - 1).getTimestamp();
            long newTimeStamp = lastTimeStamp + (long)(Math.round(Math.random()*10000));
            entryToReplicate = entryToReplicate.withTimestamp(newTimeStamp);
            entries.add(entryToReplicate);
        }
        return entries;
    }

    private List<Entry> merge(List<GpsEntry> positionEntries, List<BarometerEntry> barometerEntries) {
        int peIndex = 0;
        int beIndex = 0;
        List<Entry> entries = new ArrayList<Entry>();
        while (peIndex < positionEntries.size() && beIndex < barometerEntries.size()) {
            if (positionEntries.get(peIndex).getTimestamp() <= barometerEntries.get(beIndex).getTimestamp()) {
                entries.add(positionEntries.get(peIndex++));
            } else {
                entries.add(barometerEntries.get(beIndex++));
            }
        }
        for (; peIndex < positionEntries.size(); peIndex++) {
            entries.add(positionEntries.get(peIndex));
        }
        for (;beIndex < barometerEntries.size(); beIndex++)
            entries.add(barometerEntries.get(beIndex));
        return entries;
    }

    private List<BarometerEntry> onGenerateSomeBarometerMockData() throws NoSuchFieldException, IllegalAccessException {
        return populateMockBarometerValues();
    }

    private List<BarometerEntry> populateMockBarometerValues() throws IllegalAccessException, NoSuchFieldException {
        List<BarometerEntry> barometerEntries = new ArrayList<BarometerEntry>(1000);
        BarometerEntry be = new BarometerEntry(System.currentTimeMillis(), 93.4f);
        barometerEntries.add(be);
        for (int i = 1; i < 1000; i++) {
            BarometerEntry prevEntry = barometerEntries.get(i-1);
            barometerEntries.add((BarometerEntry) prevEntry.withTimestamp(prevEntry.getTimestamp() + 100));
        }

        return barometerEntries;
    }

    private List<GpsEntry> onGenerateSomeGpsMockData() throws NoSuchFieldException, IllegalAccessException {
        return populateMockGpsValues();
    }

    private List<GpsEntry> populateMockGpsValues() throws NoSuchFieldException, IllegalAccessException {
        List<GpsEntry> gpsEntries = new ArrayList<GpsEntry>(151);
        LatitudeSensorValue lat = new LatitudeSensorValue(35.01647199);
        LongitudeSensorValue lng = new LongitudeSensorValue(33.72351227);
        LatLng latLng = new LatLng(lat, lng);
        gpsEntries.add(GpsEntry.valueOf(System.currentTimeMillis(), latLng));
        for (int i = 1; i < 151; i++) {
            GpsEntry prevEntry = gpsEntries.get(i - 1);
            gpsEntries.add((GpsEntry)prevEntry.withTimestamp(prevEntry.getTimestamp() + 2000L));
        }
        return gpsEntries;
    }
}
