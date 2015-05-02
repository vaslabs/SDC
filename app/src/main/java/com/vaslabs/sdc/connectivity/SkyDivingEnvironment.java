package com.vaslabs.sdc.connectivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.sensors.BarometerListener;
import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.GPSSensor;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.sensors.NoBarometerException;
import com.vaslabs.sdc.ui.OnSpeechSuccessListener;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.ui.util.SkyDiverListAdapterHelper;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverEnvironmentUpdate;
import com.vaslabs.sdc.utils.SkyDiverPersonalUpdates;
import com.vaslabs.sdc.utils.SkyDiverPositionalComparator;
import com.vaslabs.sdc.logs.PositionGraph;

public class SkyDivingEnvironment extends BaseAdapter implements
        OnSpeechSuccessListener, SkyDiverEnvironmentUpdate,
        SkyDiverPersonalUpdates, BarometerListener {
    private static final String LOG_TAG = "SKYDIVING_ENVIRONMENT";
    private Map<String, SkyDiver> skydivers;
    private List<SkyDiver> skydiversList;
    private SkyDiver myself;
    private Context context;
    private static SkyDivingEnvironment environmentInstance = null;
    private SpeechCommunicationManager scm;
    private int[] colors = SkyDiverListAdapterHelper.getColors();
    private BarometerSensor barometerSensor;
    private GPSSensor gpsSensor;
    private PositionGraph positionGraph;

    private final int defaultColor = SkyDiverListAdapterHelper
            .getDefaultColor();

    private SkyDivingEnvironment( Context context ) {
        skydivers = new HashMap<String, SkyDiver>();
        this.context = context;
        UserInformation ui = UserInformation.getUserInfo( context );
        myself = new SkyDiver( ui );
        scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( context, this );
        skydiversList = new ArrayList<SkyDiver>();
        SkyDivingEnvironmentLogger.initLogger( context );
        registerSensors();
        positionGraph = new PositionGraph();
    }

    public synchronized static SkyDivingEnvironment getInstance( Context c ) {
        if ( environmentInstance == null ) {
            environmentInstance = new SkyDivingEnvironment( c );
        }
        return environmentInstance;
    }

    @Override
    public void onSuccess() {
        scm.getTalkingAvailable( context );

    }

    @Override
    public void onFailure() {
        // TODO warning

    }

    private void registerSensors() {
        try
        {
            barometerSensor = new BarometerSensor(this.context);
            barometerSensor.registerListener(this);
        } catch (NoBarometerException nbe) {
            //scm.getNoBarometerWarning();
        } catch (Exception e) {
            //scm.getBarometerErrorWarning();
        }
        try {
            gpsSensor = new GPSSensor(this.context);
        } catch (Exception e) {
            //scm.getGPSErrorWarning();
        }
    }

    @Override
    public synchronized void onNewSkydiverInfo( SkyDiver skydiver ) {
        if ( skydivers.containsKey( skydiver.getName() ) ) {
            onSkydiverInfoUpdate( skydiver );

        } else {
            skydivers.put( skydiver.getName(), skydiver );
            skydiversList.add( skydiver );
            Collections.sort( this.skydiversList,
                    new SkyDiverPositionalComparator( myself ) );
            SpeechCommunicationManager scm =
                    SpeechCommunicationManager.getInstance();
            scm.getProximityWarning( context );
            Log.v( LOG_TAG, "New connection: " + skydiver.toString() );
            SkyDivingEnvironmentLogger.Log( "New connection: " + skydiver.toString() );
        }
        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onSkydiverInfoUpdate( SkyDiver skydiver ) {
        if ( !this.skydivers.containsKey( skydiver.getName() ) ) {
            onNewSkydiverInfo(skydiver);
        } else {
            SkyDiver previouslyKnownSkyDiver =
                    this.skydivers.get( skydiver.getName() );
            if ( skydiver.getConnectivityStrengthAsInt() != previouslyKnownSkyDiver
                    .getConnectivityStrengthAsInt() ) {
                onConnectivityChange( skydiver );
            } else {
                previouslyKnownSkyDiver.updatePositionInformation( skydiver
                        .getPosition() );
                Collections.sort( this.skydiversList,
                        new SkyDiverPositionalComparator( myself ) );
                // also speed && direction which are not yet available TODO
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onConnectivityChange( SkyDiver skydiver ) {

        if ( skydiver.getConnectivityStrengthAsInt() == SDConnectivity.CONNECTION_LOST
                .ordinal() ) {
            onLooseConnection( skydiver );
            Log.v( LOG_TAG, "Lost connection: " + skydiver.toString() );
            SkyDivingEnvironmentLogger.Log("Lost connection: " + skydiver.toString());
        } else {
            SkyDiver sd = skydivers.get( skydiver.getName() );
            if (sd == null)
                return;
            if (sd.getConnectivityStrengthAsInt() == SDConnectivity.CONNECTION_LOST.ordinal())
            {
                SpeechCommunicationManager scm =
                        SpeechCommunicationManager.getInstance();
                scm.getProximityWarning(context);
            }

            sd.setConnectivityStrength( SDConnectivity.values()[skydiver
                    .getConnectivityStrengthAsInt()] );

        }

        Collections.sort( this.skydiversList, new SkyDiverPositionalComparator(
                myself ) );

        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onLooseConnection( SkyDiver skydiver ) {
        SkyDiver sd = skydivers.get( skydiver.getName() );
        if ( sd != null ) {
            SpeechCommunicationManager.getInstance().informAboutdisconnection(
                    SDConnectivity.values()[sd.getConnectivityStrengthAsInt()],
                    context );
            sd.setConnectivityStrength( SDConnectivity.CONNECTION_LOST );
        }

        this.notifyDataSetChanged();
    }

    @Override
    public synchronized void onMyAltitudeUpdate( MetersSensorValue altitude ) {

    }

    @Override
    public synchronized void onMyGPSUpdate( LatitudeSensorValue lat,
            LongitudeSensorValue lng ) {
        myself.getPosition().setLat( lat );
        myself.getPosition().setLng( lng );

    }

    public int getOtherSkyDiversSize() {
        return this.skydivers.size();
    }

    public SkyDiver getSkyDiver( int position ) {
        return skydiversList.get(position);
    }

    @Override
    public int getCount() {
        return skydiversList.size();
    }

    @Override
    public SkyDiver getItem( int position ) {
        return skydiversList.get(position);
    }

    @Override
    public long getItemId( int position ) {
        return 0;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        TextView tv = new TextView( parent.getContext() );
        int connectivity = getItem( position ).getConnectivityStrengthAsInt();
        int color =
                connectivity < colors.length ? colors[connectivity]
                        : defaultColor;
        tv.setBackgroundColor( color );
        tv.setText( getItem( position ).getName() );

        return tv;
    }

    public static SkyDivingEnvironment getInstance() {
        return environmentInstance;
    }

    @Override
    public void onLooseConnection( String skydiverKey ) {
        SkyDiver sd = skydivers.get( skydiverKey );
        onLooseConnection( sd );
    }

    public static String getLogFile() {
        return SkyDivingEnvironmentLogger.LOG_FILE;
    }

    @Override
    public void onHPASensorValueChange(HPASensorValue pressure, MetersSensorValue altitude) {
        myself.updatePositionInformation(altitude);
        positionGraph.registerBarometerValue(pressure, altitude);
    }

    public void writeSensorLogs() {
        int trailingBytes = normaliseFile();
        FileOutputStream logStream = null;
        try {
            logStream = context.openFileOutput(PositionGraph.LOG_FILE, Context.MODE_APPEND);
        } catch (FileNotFoundException fnfe) {
            return;
        }

        try {
            logStream.write(positionGraph.getBarometerData());
        } catch (IOException ioE) {

        } finally {
            if (logStream != null)
                try {
                    logStream.close();
                } catch (IOException e) {

                }
        }

    }

    private int normaliseFile() {
        FileInputStream logStream = null;
        try {
            logStream = context.openFileInput(PositionGraph.LOG_FILE);
        } catch (FileNotFoundException fnfe) {
            return 0;
        }

        int bytes = 0;
        int result = 0;
        try {

            while (result >= 0) {
                result = logStream.read();
                bytes++;
            }
            bytes = bytes % 12;
        } catch (IOException e) {
            return -1;
        } finally {
            if (logStream != null)
                try {
                    logStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return bytes;
    }

    public List<String> getSensorLogsLinesUncompressed() {
        FileInputStream logStream = null;
        try {
            logStream = context.openFileInput(PositionGraph.LOG_FILE);
        } catch (FileNotFoundException e) {
            return null;
        }

        try {
            List<String> lines = new ArrayList<String>();
            int result = 0;
            byte[] timestampBytes = new byte[8];
            byte[] meterBytes = new byte[4];
            while (result >= 0) {
                //first 8 bytes represent timestamp

                result = logStream.read(timestampBytes, 0, 8);
                if (result < 0)
                    break;
                long timestamp = (timestampBytes[0] & 0x0ff) |
                        (timestampBytes[1]& 0x0ff) << 8 |
                        (timestampBytes[2] & 0x0ff) << 16 |
                        (timestampBytes[3] & 0x0ff) << 24 |
                        (timestampBytes[4] & 0x0ff) << 32 |
                        (timestampBytes[5] & 0x0ff) << 40 |
                        (timestampBytes[6] & 0x0ff) << 48 |
                        timestampBytes[7] << 54;
                result = logStream.read(meterBytes, 0, 4);
                int meterBits = (meterBytes[0] & 0x0ff) |
                        (meterBytes[1] & 0x0ff) << 8 |
                        (meterBytes[2] & 0x0ff) << 16 |
                                 meterBytes[3] << 24;
                float meterValue = Float.intBitsToFloat(meterBits);
                lines.add(String.valueOf(timestamp) + ":" + String.valueOf(meterValue));

            }

            return lines;
        } catch (IOException ioe) {
            return null;
        }
    }
}
