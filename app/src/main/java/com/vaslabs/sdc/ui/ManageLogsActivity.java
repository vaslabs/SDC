package com.vaslabs.sdc.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.view.View.OnClickListener;

public class ManageLogsActivity extends Activity {

    private TextView logsTextView;
    private Button submitLogsButton;
    private LoginDialogFragment loginDialog;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_manage_logs );
        submitLogsButton = (Button)findViewById(R.id.submitLogsbutton);
        logsTextView = (TextView)findViewById( R.id.logsTextView );
        FileInputStream inputStream = null;
        loginDialog = new LoginDialogFragment();
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

        submitLogsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginDialog.show(getFragmentManager(), "Logs");
            }
        });
    }

    static public class LoginDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_layout, null))
                    // Add action buttons
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LoginDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

}

