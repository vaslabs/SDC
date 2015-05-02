package com.vaslabs.sdc.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.SDCLogManager;

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
        List<String> positionLogLines = sde.getSensorLogsLinesUncompressed();
        content.append('\n');
        content.append('\n');
        for (String logLine : positionLogLines) {
            content.append(logLine);
            content.append('\n');
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

class SubmitLogs extends AsyncTask<String, Void, Integer> {

    private Context context;
    protected SubmitLogs(Context context) {
        this.context = context;
    }
    @Override
    protected Integer doInBackground(String... params) {
        if (params.length != 2) {
            return HttpStatus.SC_UNAUTHORIZED;
        }
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( context.getString(R.string.remote_host));
        SDCLogManager lm = SDCLogManager.getInstance(context);
        try {
            String username = params[0];
            String password = params[1];
            lm.submitLogs(username, password);
        }
        catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return HttpStatus.SC_UNAUTHORIZED;
        }
        String message = null;
        try {
            Response response = lm.getLastResponse();
            Object responseBody = response.getBody();
            JSONObject json = (JSONObject) responseBody;
            message = json.getString("message");
            if ("OK".equals(message))
                return HttpStatus.SC_OK;
        } catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return 500;
        }
        Log.e("SDC remote server", message);
        return HttpStatus.SC_OK;
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status == HttpStatus.SC_OK) {
            Toast.makeText(context, "Success: Logs have been submitted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Error: " + status, Toast.LENGTH_LONG).show();
        }
    }
}

