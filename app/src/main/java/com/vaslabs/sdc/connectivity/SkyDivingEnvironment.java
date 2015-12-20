package com.vaslabs.sdc.connectivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.vaslabs.emergency.LandingTrendListener;
import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.sensors.BarometerListener;
import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.GPSSensor;
import com.vaslabs.sdc.sensors.GPSSensorListener;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.LatitudeSensorValue;
import com.vaslabs.sdc.sensors.LongitudeSensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.sensors.NoBarometerException;
import com.vaslabs.sdc.ui.OnSpeechSuccessListener;
import com.vaslabs.sdc.ui.SpeechCommunicationManager;
import com.vaslabs.sdc.ui.util.TrendingPreferences;
import com.vaslabs.sdc.utils.BarometerTrendStrategy;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.sdc.utils.SDConnectivity;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverEnvironmentUpdate;
import com.vaslabs.sdc.utils.SkyDiverPersonalUpdates;
import com.vaslabs.sdc.utils.SkyDiverPositionalComparator;
import com.vaslabs.sdc.logs.PositionGraph;
import com.vaslabs.vtrends.impl.AbstractTrendStrategy;
import com.vaslabs.vtrends.types.DifferentiableFloat;
import com.vaslabs.vtrends.types.TrendDirection;
import com.vaslabs.vtrends.types.TrendPoint;

public class SkyDivingEnvironment implements
        OnSpeechSuccessListener, SkyDiverEnvironmentUpdate,
        SkyDiverPersonalUpdates, BarometerListener, GPSSensorListener {
    private static final String LOG_TAG = "SKYDIVING_ENVIRONMENT";
    private Map<String, SkyDiver> skydivers;
    private List<SkyDiver> skydiversList;
    private SkyDiver myself;
    private Context context;
    private static SkyDivingEnvironment environmentInstance = null;
    private SpeechCommunicationManager scm;
    private BarometerSensor barometerSensor;
    private GPSSensor gpsSensor;
    private PositionGraph positionGraph;
    private DefaultBarometerTrendListener trendListener;
    private AbstractTrendStrategy<DifferentiableFloat> trendStrategy;

    private WirelessBroadcastReceiver mReceiver;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.ActionListener actionListener;
    private boolean hasBarometer = false;
    private boolean hasStartedScanning = false;
    private SkyDivingEnvironment( Context context ) {
        skydivers = new HashMap<String, SkyDiver>();
        this.context = context;
        UserInformation ui = UserInformation.getUserInfo( context );
        myself = new SkyDiver( ui );
        scm = SpeechCommunicationManager.getInstance();
        scm.initialiseTextToSpeech( context, this );
        skydiversList = new ArrayList<SkyDiver>();

        SkyDivingEnvironmentLogger.initLogger( context );
        positionGraph = new PositionGraph();
        registerSensors();


    }

    public static SkyDivingEnvironment getInstance( Context c ) {
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
            barometerSensor = BarometerSensor.getInstance(this.context);

            hasBarometer = !barometerSensor.isDummy;
        } catch (NoBarometerException nbe) {
            hasBarometer = false;
        }
        try {
            gpsSensor = new GPSSensor(this.context);
            gpsSensor.registerListener(this);
        } catch (Exception e) {
            //scm.getGPSErrorWarning();
        }
    }

    @Override
    public void onNewSkydiverInfo( SkyDiver skydiver ) {
        if ( skydivers.containsKey( skydiver.getName() ) ) {
            onSkydiverInfoUpdate(skydiver);

        } else {
            skydivers.put( skydiver.getName(), skydiver );
            skydiversList.add( skydiver );
            Collections.sort( this.skydiversList,
                    new SkyDiverPositionalComparator( myself ) );
            SpeechCommunicationManager scm =
                    SpeechCommunicationManager.getInstance();
            scm.getProximityWarning( context );
            Log.v( LOG_TAG, "New connection: " + skydiver.toString() );
            SkyDivingEnvironmentLogger.Log("New connection: " + skydiver.toString());
        }
    }

    @Override
    public void onSkydiverInfoUpdate( SkyDiver skydiver ) {
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
                Collections.sort(this.skydiversList,
                        new SkyDiverPositionalComparator(myself));
                // also speed && direction which are not yet available TODO
            }
        }
    }

    @Override
    public void onConnectivityChange( SkyDiver skydiver ) {

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

    }

    @Override
    public void onLooseConnection( SkyDiver skydiver ) {
        SkyDiver sd = skydivers.get( skydiver.getName() );
        if ( sd != null ) {
            SpeechCommunicationManager.getInstance().informAboutdisconnection(
                    SDConnectivity.values()[sd.getConnectivityStrengthAsInt()],
                    context);
            sd.setConnectivityStrength(SDConnectivity.CONNECTION_LOST);
        }

    }

    @Override
    public void onMyAltitudeUpdate( MetersSensorValue altitude ) {

    }

    @Override
    public void onMyGPSUpdate( LatitudeSensorValue lat,
            LongitudeSensorValue lng ) {
        myself.getPosition().setLat(lat);
        myself.getPosition().setLng(lng);

    }

    public int getOtherSkyDiversSize() {
        return this.skydivers.size();
    }

    public SkyDiver getSkyDiver( int position ) {
        return skydiversList.get(position);
    }

    public static SkyDivingEnvironment getInstance() {
        return environmentInstance;
    }

    @Override
    public void onLooseConnection( String skydiverKey ) {
        SkyDiver sd = skydivers.get( skydiverKey );
        onLooseConnection(sd);
    }

    public static String getLogFile() {
        return SkyDivingEnvironmentLogger.LOG_FILE;
    }

    @Override
    public void onHPASensorValueChange(HPASensorValue pressure, MetersSensorValue altitude, MetersSensorValue deltaAltitude) {
        myself.updatePositionInformation(altitude);
        positionGraph.registerBarometerValue(pressure, altitude, deltaAltitude);
        this.trendStrategy.acceptValue(System.currentTimeMillis() / 1000.0, new DifferentiableFloat(deltaAltitude.getRawValue()));
    }

    public void writeSensorLogs() {
        FileOutputStream logStream = null;
        try {
            logStream = context.openFileOutput(PositionGraph.BAROMETER_LOG_FILE, Context.MODE_APPEND);
        } catch (FileNotFoundException fnfe) {
            try {
                logStream = context.openFileOutput(PositionGraph.BAROMETER_LOG_FILE, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        try {
            if (positionGraph.getAltitudeValuesSize() > 100)
                logStream.write(positionGraph.getBarometerData());
        } catch (IOException ioE) {
            Toast.makeText(this.context, ioE.toString(), Toast.LENGTH_LONG).show();
        } finally {
            if (logStream != null)
                try {
                    logStream.close();
                } catch (IOException e) {

                }
        }

        try {
            logStream = context.openFileOutput(PositionGraph.GPS_LOG_FILE, Context.MODE_APPEND);
        } catch (FileNotFoundException fnfe) {
            try {
                logStream = context.openFileOutput(PositionGraph.GPS_LOG_FILE, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                Toast.makeText(this.context, e.toString(), Toast.LENGTH_LONG).show();
            }

            return;
        }

        try {
            if (positionGraph.getGpsValuesSize() > 30)
                logStream.write(positionGraph.getGPSData());
        } catch (IOException ioE) {
            Toast.makeText(this.context, ioE.toString(), Toast.LENGTH_LONG).show();

        } finally {
            if (logStream != null)
                try {
                    logStream.close();
                } catch (IOException e) {

                }
        }

        cleanUp();

    }

    private void cleanUp() {
        this.positionGraph.cleanUp();
        this.skydivers.clear();
        this.initialiseStrategies();
    }

    public static List<String> getBarometerSensorLogsLinesUncompressed(Context context) {
        FileInputStream logStream = null;
        try {
            logStream = context.openFileInput(PositionGraph.BAROMETER_LOG_FILE);
        } catch (FileNotFoundException e) {
            return null;
        }

        try {
            List<String> lines = new ArrayList<String>();
            int result = 0;
            byte[] data = new byte[16];
            while ((result = logStream.read(data, 0, 16)) >= 0) {
                ByteBuffer bf = ByteBuffer.wrap(data);
                long timestamp = bf.getLong();
                float meterValue = bf.getFloat();
                float deltaMeterValue = bf.getFloat();
                lines.add(String.valueOf(timestamp) + ":" + String.valueOf(meterValue) + ","+String.valueOf(deltaMeterValue));

            }

            return lines;
        } catch (IOException ioe) {
            return null;
        }
    }


    public static List<String> getGPSSensorLogsLinesUncompressed(Context context) {
        FileInputStream logStream = null;
        try {
            logStream = context.openFileInput(PositionGraph.GPS_LOG_FILE);
        } catch (FileNotFoundException e) {
            return null;
        }

        try {
            List<String> lines = new ArrayList<String>();
            int result = 0;
            byte[] data = new byte[24];

            while (result >= 0) {
                //first 8 bytes represent timestamp

                result = logStream.read(data, 0, 24);
                if (result < 0)
                    break;
                ByteBuffer bf = ByteBuffer.wrap(data);

                long timestamp = bf.getLong();
                double latValue = bf.getDouble();
                double lngValue = bf.getDouble();
                lines.add(String.valueOf(timestamp) + ":" + String.valueOf(latValue) + ',' +
                    String.valueOf(lngValue));

            }

            return lines;
        } catch (IOException ioe) {
            return null;
        }
    }

    public String getLastPositionMessage() {
        return positionGraph.getLastPosition().toString();
    }

    public void logLanding() {
        SkyDivingEnvironmentLogger.Log("Landed");
    }

    private static class BarometerTrendOnDiveAltitudeListener extends DefaultBarometerTrendListener {



        @Override
        public void onTrendEvent() {
            environmentInstance.beginScanning();
        }

    }

    @Override
    public void onLatLngChange(LatitudeSensorValue lat, LongitudeSensorValue lng) {
        positionGraph.registerGPSValue(lat, lng);
    }

    public void registerWirelessManager(WifiP2pManager mManager, WifiP2pManager.Channel channel, WifiP2pManager.ActionListener listener) {

        this.mManager = mManager;
        this.mChannel = channel;
        this.actionListener = listener;

        if (!hasBarometer) {
            beginScanning();
            return;
        }

        initialiseStrategies();


    }

    private void initialiseStrategies() {
        TrendingPreferences tp = TrendingPreferences.getInstance();
        trendListener = new BarometerTrendOnDiveAltitudeListener();
        trendListener.forDirectionAction(TrendDirection.DOWN);
        trendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(tp.altitudeLimit), 0.0));
        trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(tp.altitudeSensitivity, tp.timeDensity, 1+(int) (5000/tp.altitudeSensitivity));
        trendStrategy.registerEventListener(trendListener);

        LandingTrendListener landingTrendListener = new LandingTrendListener(this.context);
        landingTrendListener.forDirectionAction(TrendDirection.DOWN);
        landingTrendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(150f), 0.0));
        trendStrategy.registerEventListener(landingTrendListener);

        barometerSensor.registerListener(this);
    }

    private void beginScanning() {
        if (!hasStartedScanning) {
            hasStartedScanning = true;
            this.mManager.discoverPeers(this.mChannel, this.actionListener);
            SkyDivingEnvironmentLogger.Log("Started scanning");
        }
    }

    public boolean hasBarometer() {
        return this.hasBarometer;
    }
}


