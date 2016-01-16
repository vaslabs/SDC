package com.vaslabs.sdc.connectivity;

import com.android.volley.Response;
import com.vaslabs.accounts.Account;

import org.json.JSONObject;

/**
 * Created by vnicolaou on 12/01/16.
 */
public interface SdcService {
    void createTemporaryAccount(Account account, com.android.volley.Response.Listener<JSONObject> listener,
                                  com.android.volley.Response.ErrorListener errorListener);

    void getSession(String api_token, int sessionId, com.android.volley.Response.Listener<String> sessionListFetcherListener,
                                    com.android.volley.Response.ErrorListener errorListener);


    void submitSession(String apiToken, String json, com.android.volley.Response.Listener<JSONObject> listener,
                                 com.android.volley.Response.ErrorListener errorListener);

    void getSessionList(String apiToken, com.android.volley.Response.Listener<String> sessionListFetcherListener, com.android.volley.Response.ErrorListener errorListener);

    void getSessionData(String apiToken, Response.Listener<String> sessionFetcherListener, Response.ErrorListener errorListener);
}
