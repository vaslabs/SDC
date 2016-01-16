package com.vaslabs.sdc.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.AccountManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.ui.R;

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
    private SdcService sdcService;
    private Account account;

    public ManageLogsFragment() {
        // Required empty public constructor
    }

    public static ManageLogsFragment newInstance(String param1, String param2) {
        ManageLogsFragment fragment = new ManageLogsFragment();
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
                    SDCLogManager sdcLogManager = SDCLogManager.getInstance(getActivity());
                    sdcLogManager.submitLogs(sdcService);
                }
            });
            logsTextView.setText(content.toString());
        } catch ( IOException e ) {
            logsTextView.setText(e.toString());
            fab.hide();
        }
        AccountManager accountManager = new AccountManager(getActivity());
        try {
            account = accountManager.getAccount();
        } catch (Exception e) {
            account = null;
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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