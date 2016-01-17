package com.vaslabs.sdc.connectivity.impl;

import android.util.Log;

import com.google.gson.JsonObject;
import com.vaslabs.accounts.Account;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vnicolaou on 15/01/16.
 */
public class CreateAccountRQ {
    private String publicKey;

    public CreateAccountRQ(Account account) {
        publicKey = account.getKey();
    }

    public JSONObject toJsonObject() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("publicKey", publicKey);
        } catch (JSONException je) {
            Log.e("CreateAccountRQ", je.getMessage());
        }
        return jo;
    }
}
