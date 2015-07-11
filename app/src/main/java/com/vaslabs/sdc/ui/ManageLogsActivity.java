package com.vaslabs.sdc.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.structs.DateStruct;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

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
        StringBuilder content = new StringBuilder(1024);
        try {
            inputStream = this.openFileInput( SkyDivingEnvironment.getLogFile() );
            BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );

            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append( line ).append( '\n' );
            }


            
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

        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(this);
        List<String> positionLogLines = sde.getBarometerSensorLogsLinesUncompressed();
        if (positionLogLines != null) {
            content.append('\n');
            content.append('\n');
            for (String logLine : positionLogLines) {
                content.append(logLine);
                content.append('\n');
            }
        }

        content.append('\n');
        content.append('\n');

        positionLogLines = sde.getGPSSensorLogsLinesUncompressed();
        if (positionLogLines != null) {
            content.append('\n');
            content.append('\n');
            for (String logLine : positionLogLines) {
                content.append(logLine);
                content.append('\n');
            }
        }
        logsTextView.setText(content.toString());

        submitLogsButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginDialog.show(getFragmentManager(), "Logs");
            }
        });
    }

    static public class LoginDialogFragment extends DialogFragment {
        private EditText usernameEditText;
        private EditText passwordEditText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View inflate = inflater.inflate(R.layout.dialog_layout, null);
            usernameEditText = (EditText) inflate.findViewById(R.id.usernameEditText);
            passwordEditText = (EditText) inflate.findViewById(R.id.passwordEditText);
            builder.setView(inflate)
                    // Add action buttons
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            (new SubmitLogs(getActivity()))
                                       .execute(usernameEditText.getText().toString(),
                                               passwordEditText.getText().toString()
                                       );
                            Toast.makeText(getActivity(), "Submitting logs...", Toast.LENGTH_SHORT).show();
                            LoginDialogFragment.this.getDialog().dismiss();
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

class SubmitLogs extends AsyncTask<String, Void, String> {

    private Context context;
    protected SubmitLogs(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( context.getString(R.string.remote_host));
        SDCLogManager lm = SDCLogManager.getInstance(context);
        try {
            InputStreamReader jsonReader = new InputStreamReader(lm.openLogs());
            String jsonString = LogUtils.parse(jsonReader);
            Gson gson = new Gson();
            SkydivingSessionData sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
            Map<DateStruct, SkydivingSessionData> sessionDates = SessionFilter.filter(sessionData);
            sessionData = SessionFilter.mostRecent(sessionDates);
            SDCLogManager logManager = SDCLogManager.getInstance(context);
            logManager.submitLogs(sessionDates);
            try {
                lm.saveLatestSession(sessionData);
            } catch (IOException ioe) {
                Log.e("SDLCLogManager", ioe.toString());
            }
        }
        catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return e.toString();
        }
        String message = null;
        try {
            Response response = lm.getLastResponse();
            Object responseBody = response.getBody();
            JSONObject json = (JSONObject) responseBody;
            message = json.getString("message");
            return message;
        } catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String status) {
        if ("OK".equals(status)) {
            Toast.makeText(context, "Success: Logs have been submitted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Error: " + status, Toast.LENGTH_LONG).show();
        }
    }
}

