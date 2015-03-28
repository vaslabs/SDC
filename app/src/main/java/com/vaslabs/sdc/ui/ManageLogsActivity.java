package com.vaslabs.sdc.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ManageLogsActivity extends Activity {

    private TextView logsTextView;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_manage_logs );
        
        logsTextView = (TextView)findViewById( R.id.logsTextView );
        FileInputStream inputStream = null;
        try {
            inputStream = this.openFileInput( SkyDivingEnvironment.getLogFile() );
            BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );
            StringBuilder content = new StringBuilder(1024);
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append( line ).append( '\n' );
            }
            
            logsTextView.setText( content.toString() );
            
        } catch ( FileNotFoundException e ) {
            logsTextView.setText( e.toString() );
        }
        catch (IOException e) {
            logsTextView.setText( e.toString() );            
        }
        finally  {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch ( IOException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
}
