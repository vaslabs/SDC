package com.vaslabs.emergency;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;
import com.vaslabs.units.TimeUnit;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class EmergencyPreferences {

    private static final String FILE_NAME = "emergencypreferences.json";
    private static final double DEFAULT_MINUTES_LIMIT = 5;

    private final TimeUnit timeUnit = TimeUnit.MINUTES;

    @Expose
    private double minimumTimeBeforeCall;
    @Expose
    private List<EmergencyContact> emergencyContactList;

    private Context context;

    private EmergencyPreferences() {

    }

    private EmergencyPreferences(double time) {
        minimumTimeBeforeCall = time;
        emergencyContactList = new ArrayList<EmergencyContact>();
    }

    public static EmergencyPreferences load(Context mContext) {
        Reader emergencyPreferencesReader = null;
        EmergencyPreferences ep = null;
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            InputStream emergencyPreferencesInputStream = mContext.openFileInput(EmergencyPreferences.FILE_NAME);
            emergencyPreferencesReader = new InputStreamReader(emergencyPreferencesInputStream);
            ep = gson.fromJson(emergencyPreferencesReader, EmergencyPreferences.class);
            if (ep == null) {
                ep = new EmergencyPreferences(EmergencyPreferences.DEFAULT_MINUTES_LIMIT);
            }
        } catch (IOException ioe) {
            ep = new EmergencyPreferences(EmergencyPreferences.DEFAULT_MINUTES_LIMIT);
        } finally {
            if (emergencyPreferencesReader != null) {
                try {
                    emergencyPreferencesReader.close();
                } catch (IOException e) {
                }
            }
        }
        ep.context = mContext;
        return ep;
    }

    public double getMinimumTimeBeforeCall(TimeUnit timeunit) {
        return timeunit.convert(minimumTimeBeforeCall, this.timeUnit);
    }

    public List<EmergencyContact> getEmergencyContactList() {
        return emergencyContactList;
    }

    public void addEmergencyContact(String name, String phoneNumber) throws Exception {
        this.emergencyContactList.add(new EmergencyContact(name, phoneNumber));
        this.save();
    }

    protected void save() throws Exception {
        PrintWriter writer = new PrintWriter(context.openFileOutput(EmergencyPreferences.FILE_NAME, Context.MODE_PRIVATE));
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(this);
        writer.print(json);
        writer.close();
    }

    public void setMinimumTimeBeforeEmergencyCall(double v, TimeUnit timeUnit) throws Exception {
        this.minimumTimeBeforeCall = this.timeUnit.convert(v, timeUnit);
        this.save();
    }
}
