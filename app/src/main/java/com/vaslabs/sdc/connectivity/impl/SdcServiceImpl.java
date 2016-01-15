package com.vaslabs.sdc.connectivity.impl;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vnicolaou on 14/01/16.
 */
public class SdcServiceImpl implements SdcService{
    private final String url;
    private static final String CREATE_ACCOUNT = "/create_account/";
    private static final String SUBMIT_SESSION = "/submit_session/";
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
    public void submitSession(String apiToken, String json, com.android.volley.Response.Listener<JSONObject> listener,
                                        com.android.volley.Response.ErrorListener errorListener) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JsonObjectRequestWithHeaders jor = new JsonObjectRequestWithHeaders(
                    this.url + SUBMIT_SESSION, jsonObject, listener, errorListener
            );
            jor.addApiToken(apiToken);

            requestQueue.add(jor);
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
            return;
        }

    }

    private class JsonObjectRequestWithHeaders extends JsonObjectRequest {

        private Map<String, String> headers;

        public JsonObjectRequestWithHeaders(String url, JSONObject jsonRequest, com.android.volley.Response.Listener<JSONObject> listener, com.android.volley.Response.ErrorListener errorListener) {
            super(Request.Method.POST, url, jsonRequest, listener, errorListener);
            headers = new HashMap<String, String>();
        }

        private void addApiToken(String apiToken) {
            headers.put("Authorization", "Token " + apiToken);
        }

        @Override
        public Map<String, String> getHeaders() {
            return headers;
        }
    }
}
