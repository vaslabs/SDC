package com.vaslabs.sdc.connectivity.impl;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.RequestOutcome;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.connectivity.SdcService;

import org.json.JSONObject;

/**
 * Created by vnicolaou on 14/01/16.
 */
public class SdcServiceImpl implements SdcService{
    private final String url;
    private static final String CREATE_ACCOUNT = "/create_account/";
    private RequestQueue requestQueue;

    public SdcServiceImpl(String host, Context context) {
        this(host, "https://", context);
    }


    public SdcServiceImpl(String host, String protocol, Context context) {
        this.url = protocol + host + "/android";
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void createTemporaryAccount(Account account, com.android.volley.Response.Listener<JSONObject> listener,
                                       com.android.volley.Response.ErrorListener errorListener) {
        CreateAccountRQ createAccountRQ = new CreateAccountRQ(account);
        JSONObject createAccountJsonObject = createAccountRQ.toJsonObject();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                this.url + CREATE_ACCOUNT,
                createAccountJsonObject,
                listener, errorListener);
        requestQueue.add(request);
    }

    @Override
    public int getNumberOfSessions(String api_token) {
        return 0;
    }

    @Override
    public SkydivingSessionData getSession(String api_token) {
        return null;
    }

    @Override
    public String getApiToken(Account account, int id) {
        return null;
    }

    @Override
    public RequestOutcome submitSession(String apiToken, String json) {
        return null;
    }
}
