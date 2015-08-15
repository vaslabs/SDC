package com.vaslabs.sdc.ui;

import com.gc.materialdesign.views.ButtonRectangle;
import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.sensors.BarometerListener;
import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.sensors.NoBarometerException;
import com.vaslabs.sdc.ui.util.TrendingPreferences;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends Activity implements BarometerListener {

    private EditText massEditText;
    private EditText nameEditText;
    private EditText seaLevelEditText;
    private EditText altitudeLimitEditText;

    private ButtonRectangle currentPressureButton;
    private ButtonRectangle saveButton;
    private ButtonRectangle cancelButton;

    private BarometerSensor barometer;
    private Context meterSensitivityOptions;
    private List<Integer> meterOptions;
    private List<Double> densityOptions;

    UserInformation userInfo;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        massEditText = (EditText) this.findViewById( R.id.massEditText );
        nameEditText = (EditText)this.findViewById( R.id.nameEditText );
        seaLevelEditText = (EditText)this.findViewById( R.id.seaLevelEditText );

        altitudeLimitEditText = (EditText)this.findViewById(R.id.altitudeLimitEditText);
        meterOptions = new ArrayList<Integer>();
        meterOptions.add(10);
        meterOptions.add(20);
        meterOptions.add(50);
        meterOptions.add(100);
        densityOptions = new ArrayList<Double>();
        densityOptions.add(0.5);
        densityOptions.add(1.0);
        densityOptions.add(2.0);
        densityOptions.add(5.0);
        userInfo = UserInformation.getUserInfo( this );
        
        updateTextFields();
        
        currentPressureButton = (com.gc.materialdesign.views.ButtonRectangle)this.findViewById( R.id.currentPressureButton );
        saveButton = (com.gc.materialdesign.views.ButtonRectangle)this.findViewById( R.id.saveButton );
        cancelButton = (com.gc.materialdesign.views.ButtonRectangle)this.findViewById( R.id.cancelButton );
        
        try {
            barometer = BarometerSensor.getInstance(this);

            barometer.registerListener(this);
        }
        catch (NoBarometerException nbe) {
            Toast.makeText(this, nbe.toString(), Toast.LENGTH_SHORT).show();
        }
        saveButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                float altitudeLimit = 1000;
                try {
                    altitudeLimit = Float.parseFloat(altitudeLimitEditText.getText().toString());
                } catch (NumberFormatException nfe) {
                    Toast.makeText(v.getContext(), nfe.getMessage() + ": Defaults to 1000", Toast.LENGTH_SHORT).show();
                }
                UserPreferences up = new UserPreferences();
                try {
                    up.mass = Float.parseFloat( massEditText.getText().toString() );
                } catch (NumberFormatException nfe) {
                    up.mass = userInfo.getMass();
                }
                up.name = nameEditText.getText().toString();
                try {
                    up.seaLevel = Float.parseFloat( seaLevelEditText.getText().toString() );
                } catch (NumberFormatException nfe) {
                    up.seaLevel = userInfo.getSeaLevelCalibration();
                }
                
                UserInformation.setUserPreferences( v.getContext(), up );
                userInfo = UserInformation.getUserInfo( v.getContext() );
                updateTextFields();
                
            }
        } );
        
        cancelButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                updateTextFields();
            }
        } );
        
        currentPressureButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                seaLevelEditText.setText( currentPressureButton.getText().toString() );
            }
        } );
        
    }

    private void attachOptionsMeters(Spinner spinnerMeterSensitivity) {
        ArrayAdapter meterOptionsAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item);
        meterOptionsAdapter.addAll(meterOptions);
        spinnerMeterSensitivity.setAdapter(meterOptionsAdapter);
    }

    private void attachOptionsTimeDensity(Spinner spinnerTimeDensity) {
        ArrayAdapter densityOptionsAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item);
        densityOptionsAdapter.addAll(densityOptions);
        spinnerTimeDensity.setAdapter(densityOptionsAdapter);
    }

    private void updateTextFields() {
        massEditText.setText( String.valueOf(userInfo.getMass()) );
        nameEditText.setText( userInfo.getName() );
        seaLevelEditText.setText( String.valueOf(userInfo.getSeaLevelCalibration()) );
        
    }

    @Override
    public void onHPASensorValueChange( HPASensorValue pressure, MetersSensorValue altitude) {
        currentPressureButton.setText(pressure.toString());
    }
}
