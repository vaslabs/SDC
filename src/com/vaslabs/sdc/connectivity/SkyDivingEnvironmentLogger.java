package com.vaslabs.sdc.connectivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class SkyDivingEnvironmentLogger {
    private static SkyDivingEnvironmentLogger logger;
    protected final static String LOG_FILE = "sde.log";
    private static final String LOG_TAG = "SkyDivingEnvironment";
    FileOutputStream logStream;
    private final Context context;
    private SkyDivingEnvironmentLogger(Context c) {
        this.context = c;
    }
    
    protected static void initLogger(Context c) {
        if (logger == null) {
            logger = new SkyDivingEnvironmentLogger( c );
        }
    }
    
    protected static void Log(String logLine) {
        synchronized (logger) {
            logger.writeLog(logLine);
        }
    }

    protected void writeLog( String logLine ) {
        String logMsg = String.format( "%s: %s", System.currentTimeMillis(), logLine );
        
        try {
            logStream = context.openFileOutput( LOG_FILE, Context.MODE_APPEND );
            logStream.write( logMsg.getBytes());
        } catch ( IOException e ) {
            Log.wtf( LOG_TAG, e.getMessage() );
        } finally {
            try {
                logStream.close();
            } catch ( IOException e ) {
                Log.wtf( LOG_TAG, e.getMessage() );
            }
        }
    }
}
