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
    private String name;

    public enum UserEntries {
        NAME, MASS, SEA_LEVEL
    };

    private UserInformation( float mass, float seaLevel, String name ) {
        this.mass = mass;
        seaLevelCalibration = new HPASensorValue(seaLevel);
        this.name = name;
    }

    public static UserInformation getUserInformationClone( UserInformation ui ) {
        return new UserInformation( ui.getMass(), ui.getSeaLevelCalibration(),
                ui.getName() );
    }

    public static UserInformation getUserInformationFromUserPreferences(
            UserPreferences up ) {
        return new UserInformation( up.mass, up.seaLevel, up.name );
    }

    public static UserInformation getUserInfo( Context context ) {
        try {
            FileInputStream fos = context.openFileInput( USER_INFO_FILE_NAME );
            byte[] buffer = new byte[255];
            fos.read( buffer );
            fos.close();
            String entry = new String( buffer );
            String[] entryValues = entry.split( UI_ENTRY_SEPARATOR );

            String massEntry =
                    getEntryValueSafely( entryValues,
                            UserEntries.MASS.ordinal() );
            String seaLevelCalibrationEntry =
                    getEntryValueSafely( entryValues,
                            UserEntries.SEA_LEVEL.ordinal() );

            String name =
                    getEntryValueSafely( entryValues,
                            UserEntries.NAME.ordinal() );
            float mass = -1f;
            if ( massEntry != null ) {
                try {
                    mass = Float.parseFloat( massEntry );
                } catch ( NumberFormatException nfe ) {
                }
            }
            float seaLevel = -1f;
            if ( seaLevelCalibrationEntry != null ) {
                try {
                    seaLevel = Float.parseFloat( seaLevelCalibrationEntry );
                } catch ( NumberFormatException nfe ) {
                }
            }
            if ( mass < 0 || seaLevel < 0 || name == null )
                initialiseFirstTime( context );
            else
                ui = new UserInformation( mass, seaLevel, name );

        } catch ( FileNotFoundException fnfe ) {
            initialiseFirstTime( context );
        } catch ( IOException e ) {
            initialiseFirstTime( context );
        } catch ( ArrayIndexOutOfBoundsException aioobe ) {
            initialiseFirstTime( context );
        }

        return ui;
    }

    private static String
            getEntryValueSafely( String[] entryValues, int ordinal ) {
        if ( ordinal >= 0 && ordinal < entryValues.length ) {
            return entryValues[ordinal];
        }

        return null;
    }

    private static void initialiseFirstTime( Context context ) {
        float mass = 50f;
        float seaLevel = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
        ui = new UserInformation( mass, seaLevel, null );
        ui.save( context );
    }

    private void save( Context context ) {
        String data =
                String.format( "%s%s%.2f%s%.2f", ui.name, UI_ENTRY_SEPARATOR,
                        ui.mass, UI_ENTRY_SEPARATOR,
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
            ui = new UserInformation( up.mass, up.seaLevel, up.name );
        } else {
            ui.mass = up.mass;
            ui.seaLevelCalibration = new HPASensorValue( up.seaLevel );
            ui.name = up.name;
        }
        ui.save( c );
    }

    public float getMass() {
        return mass;
    }

    public float getSeaLevelCalibration() {
        return seaLevelCalibration.getRawValue();
    }

    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits( mass );
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( ! ( obj instanceof UserInformation ) ) {
            return false;
        }
        UserInformation other = (UserInformation) obj;
        if ( Float.floatToIntBits( mass ) != Float.floatToIntBits( other.mass ) ) {
            return false;
        }
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        } else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
