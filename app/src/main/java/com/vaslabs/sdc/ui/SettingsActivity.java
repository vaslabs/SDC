package com.vaslabs.sdc.ui;

import com.vaslabs.sdc.UserInformation;
import com.vaslabs.sdc.UserPreferences;
import com.vaslabs.sdc.sensors.BarometerListener;
import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.HPASensorValue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity implements BarometerListener {

    private EditText massEditText;
    private EditText nameEditText;
    private EditText seaLevelEditText;
    private Button currentPressureButton;
    private Button saveButton;
    private Button cancelButton;
    UserInformation userInfo;
    private BarometerSensor barometer;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        
        massEditText = (EditText) this.findViewById( R.id.massEditText );
        nameEditText = (EditText)this.findViewById( R.id.nameEditText );
        seaLevelEditText = (EditText)this.findViewById( R.id.seaLevelEditText );
        
        userInfo = UserInformation.getUserInfo( this );
        
        updateTextFields();
        
        currentPressureButton = (Button)this.findViewById( R.id.currentPressureButton );
        saveButton = (Button)this.findViewById( R.id.saveButton );
        cancelButton = (Button)this.findViewById( R.id.cancelButton );
        
        barometer = new BarometerSensor( this );
        barometer.registerListener( this );
        
        saveButton.setOnClickListener( new View.OnClickListener() {
            
            @Override
            public void onClick( View v ) {
                
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
    
    private void updateTextFields() {
        massEditText.setText( String.valueOf(userInfo.getMass()) );
        nameEditText.setText( userInfo.getName() );
        seaLevelEditText.setText( String.valueOf( userInfo.getSeaLevelCalibration()) );
        
    }

    @Override
    public void onHPASensorValueChange( HPASensorValue value ) {
        currentPressureButton.setText( value.toString() );
    }
}
