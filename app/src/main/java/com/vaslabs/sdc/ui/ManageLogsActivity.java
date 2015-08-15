package com.vaslabs.sdc.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.gson.Gson;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.logs.utils.SessionFilter;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc_dashboard.API.API;
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
    private ButtonRectangle submitLogsButton;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_manage_logs );
        submitLogsButton = (ButtonRectangle)findViewById(R.id.submitLogsbutton);
        logsTextView = (TextView)findViewById( R.id.logsTextView );
        StringBuilder content = null;
        try {
            SDCLogManager logManager = SDCLogManager.getInstance(this);
            List<String> logLines = logManager.loadLogs();
            content = new StringBuilder(255*logLines.size());
            for (String line : logLines)
                content.append( line ).append( '\n' );

        } catch ( IOException e ) {
            logsTextView.setText(e.toString());
        }
        if (content != null)
            logsTextView.setText(content.toString());
        else {
            logsTextView.setText(getString(R.string.nologsmessage));
        }
        submitLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    (new SubmitLogs(view.getContext()))
                            .execute();
            }
        });
    }

}

class SubmitLogs extends AsyncTask<Void, Void, String> {

    private Context context;
    protected SubmitLogs(Context context) {
        this.context = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( context.getString(R.string.remote_host));
        SDCLogManager lm = SDCLogManager.getInstance(context);
        try {
            lm.manageLogSubmission();
        }
        catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return e.toString();
        }
        String message = null;
        try {
            Response[] responses = lm.getResponses();
            message = buildMessage(responses);
            return message;
        } catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            return e.toString();
        }
    }

    private String buildMessage(Response[] responses) {
        int skipped = 0;
        int notOk = 0;
        for (Response response : responses) {

            if (response.getCode() == Response.SKIPPED)
            {
                skipped++;
            } else if (response.getCode() != HttpStatus.SC_OK ) {
                notOk++;
            }
        }
        if (notOk == 0)
            return "OK";
        return "" + notOk + " out of " + (responses.length - skipped) + " failed submission";
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

