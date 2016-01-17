package com.vaslabs.sdc.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.AccountManager;
import com.vaslabs.accounts.SDCAccount;
import com.vaslabs.accounts.TemporaryAccount;
import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.cache.CacheManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.types.LogbookSummaryEntry;
import com.vaslabs.sdc.ui.Main2Activity;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.ui.fragments.actions.ValidationActionManager;
import com.vaslabs.sdc.ui.util.DividerItemDecoration;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CardViewFragment extends Fragment implements ICardViewFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    protected RecyclerView recyclerView;
    protected View view;
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT);
        }
    };
    private String apiToken = null;

    private Response.Listener<JSONObject> accountCreationListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                apiToken = response.getString("apitoken");
                EncryptionManager encryptionManager = new EncryptionManager();
                try {
                    apiToken = encryptionManager.decrypt(apiToken, getActivity());
                    API.saveApiToken(getActivity(), apiToken);
                    initSessionData();
                } catch (Exception e) {
                    Log.e("encryption", e.getMessage());
                }
            } catch (JSONException e) {
                apiToken = null;
            }
        }
    };
    private Response.Listener<String> sessionFetcherListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Gson gson = new Gson();
                SkydivingSessionData[] skydivingSessionDatas = gson.fromJson(response, SkydivingSessionData[].class);
                CacheManager.getInstance(getActivity()).cache(apiToken, response);
                Main2Activity.sessions = skydivingSessionDatas;
                prepareUI();
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.error_fetching_data), Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void prepareUI() {
        LogbookStats[] logbookStats = LogbookStats.generateLogbookStats(Main2Activity.sessions);
        LogbookSummary logbookSummary = LogbookSummary.fromLogbookEntries(logbookStats);
        CardViewAdapter cardViewAdapter = new CardViewAdapter(LogbookSummaryEntry.fromLogbookSummary(logbookSummary, getActivity()));
        recyclerView.setAdapter(cardViewAdapter);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CardViewFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CardViewFragment newInstance(int columnCount) {
        CardViewFragment fragment = new CardViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cardview_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        special();
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ValidationActionManager fabActionManager = new ValidationActionManager();
    @Override
    public void special() {
        Context context = recyclerView.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        AccountManager accountManager = new AccountManager(getActivity());
        try {
            Account account = accountManager.getAccount();
            if (account instanceof SDCAccount) {
                apiToken = account.getKey();
                initSessionData();
            }
            else {
                createAccount((TemporaryAccount)account);
            }
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.error_creating_account) + ": " + e.getMessage(), Toast.LENGTH_LONG);
        }

        FloatingActionButton startNewSessionFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab_start_new);
        startNewSessionFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fabActionManager.manageAction(getActivity());
            }
        });
    }

    private void createAccount(TemporaryAccount account) {
        SdcService sdcService = Main2Activity.sdcService;
        sdcService.createTemporaryAccount(account, accountCreationListener, errorListener);
    }

    private void initSessionData() {
        try {
            apiToken = API.getApiToken(getActivity());
        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        SdcService sdcService = Main2Activity.sdcService;
        SkydivingSessionData[] skydivingSessionData = CacheManager.getInstance(getActivity()).getSessionData();
        if (skydivingSessionData == null)
            sdcService.getSessionData(apiToken, sessionFetcherListener, errorListener);
        else {
            Main2Activity.sessions = skydivingSessionData;
            prepareUI();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(LogbookSummary logbookSummary);
    }
}
