package com.vaslabs.sdc.connectivity;

import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.RequestOutcome;
import com.vaslabs.logbook.SkydivingSessionData;

import org.json.JSONObject;

/**
 * Created by vnicolaou on 12/01/16.
 */
public interface SdcService {
    void createTemporaryAccount(Account account, com.android.volley.Response.Listener<JSONObject> listener,
                                  com.android.volley.Response.ErrorListener errorListener);

    int getNumberOfSessions(String api_token);

    SkydivingSessionData getSession(String api_token);

    String getApiToken(Account account, int id);

    void submitSession(String apiToken, String json, com.android.volley.Response.Listener<JSONObject> listener,
                                 com.android.volley.Response.ErrorListener errorListener);
}
