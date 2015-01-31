package com.vaslabs.sdc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.hardware.SensorManager;

import com.vaslabs.sdc.sensors.HPASensorValue;

public class UserInformation {
    private float mass;
    private HPASensorValue seaLevelCalibration;
    private static UserInformation ui;
    private static final String USER_INFO_FILE_NAME = "ui.info";
    private static final String UI_ENTRY_SEPARATOR = " , ";

    public enum UserEntries {
        MASS, SEA_LEVEL
    };

    private UserInformation( float mass, float seaLevel ) {
        this.mass = mass;
        seaLevelCalibration = new HPASensorValue();
        seaLevelCalibration.setRawValue( seaLevel );
    }
    
    public static UserInformation getUserInformationClone(UserInformation ui) {
        return new UserInformation(ui.getMass(), ui.getSeaLevelCalibration());
    }

    public static UserInformation getUserInfo( Context context ) {
        try {
            FileInputStream fos = context.openFileInput( USER_INFO_FILE_NAME );
            byte[] buffer = new byte[255];
            fos.read( buffer );
            fos.close();
            String entry = new String( buffer );
            String[] entryValues = entry.split( UI_ENTRY_SEPARATOR );
            String massEntry = entryValues[UserEntries.MASS.ordinal()];
            String seaLevelCalibrationEntry =
                    entryValues[UserEntries.SEA_LEVEL.ordinal()];

            float mass = Float.parseFloat( massEntry );
            float seaLevel = Float.parseFloat( seaLevelCalibrationEntry );
            ui = new UserInformation( mass, seaLevel );

        } catch ( FileNotFoundException fnfe ) {
            initialiseFirstTime( context );
        } catch ( IOException e ) {
            initialiseFirstTime( context );
        } catch ( ArrayIndexOutOfBoundsException aioobe ) {
            initialiseFirstTime( context );
        }

        return ui;
    }

    private static void initialiseFirstTime( Context context ) {
        float mass = 50f;
        float seaLevel = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;

        ui = new UserInformation( mass, seaLevel );
        ui.save( context );
    }

    private void save( Context context ) {
        String data =
                String.format( "%.2f , %.2f", ui.mass,
                        ui.seaLevelCalibration.getRawValue() );

        FileOutputStream fos = null;

        try {
            fos =
                    context.openFileOutput( USER_INFO_FILE_NAME,
                            Context.MODE_PRIVATE );
            try {
                fos.write( data.getBytes() );
            } catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch ( IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch ( FileNotFoundException e1 ) {
            // this shouldn't be thrown but if everything goes wrong
            // if save fails, default data will be used but will be
            // volatile
        }

    }

    public static void setUserPreferences( Context c, UserPreferences up ) {
        if ( ui == null ) {
            ui = new UserInformation( up.mass, up.seaLevel );
        } else {
            ui.mass = up.mass;
            ui.seaLevelCalibration.setRawValue( up.seaLevel );
        }
        ui.save( c );
    }

    public float getMass() {
        // TODO Auto-generated method stub
        return mass;
    }

    public float getSeaLevelCalibration() {
        return seaLevelCalibration.getRawValue();
    }

}
