package com.vaslabs.sdc.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.ui.R;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageLogsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageLogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageLogsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private TextView logsTextView;

    public ManageLogsFragment() {
        // Required empty public constructor
    }

    public static ManageLogsFragment newInstance(String param1, String param2) {
        ManageLogsFragment fragment = new ManageLogsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_logs, container, false);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab_submit);
        logsTextView = (TextView)view.findViewById(R.id.manage_logs_text_view);
        StringBuilder content = null;
        try {
            SDCLogManager logManager = SDCLogManager.getInstance(view.getContext());
            List<String> logLines = logManager.loadLogs();
            content = new StringBuilder(100*logLines.size());
            for (String line : logLines)
                content.append( line ).append( '\n' );

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SubmitLogs(v.getContext()).execute();
                }
            });

        } catch ( IOException e ) {
            logsTextView.setText(e.toString());
            fab.hide();
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

class SubmitLogs extends AsyncTask<Void, Void, String> {

    private Context context;
    protected SubmitLogs(Context context) {
        this.context = context;
    }
    private String message;
    @Override
    protected String doInBackground(Void... params) {
        CommunicationManager.getInstance(context);
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
            this.message = message;
            return message;
        } catch (Exception e) {
            Log.e("Submitting logs", e.toString());
            this.message = e.toString();
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
        if (notOk == 0 && skipped == 0)
            return "OK";
        else if (skipped > 0) {
            return "Skipped: " + skipped + " sessions because of not enough data entries";
        }
        return "" + notOk + " out of " + (responses.length - skipped) + " failed submission";
    }

    @Override
    protected void onPostExecute(String status) {
        if ("OK".equals(status)) {
            Toast.makeText(context, "Success: Logs have been submitted. " + this.message, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Error: " + status + ". ", Toast.LENGTH_LONG).show();
        }
    }
}