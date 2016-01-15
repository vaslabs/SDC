package com.vaslabs.accounts;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.sdc.connectivity.SkydivingSessionListEntry;
import com.vaslabs.sdc.ui.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vnicolaou on 15/01/16.
 */
public class TestFetchListOfSubmittedSessions extends AndroidTestCase {
    private String apiToken;
    private CountDownLatch countDownLatch;
    Response.Listener<JSONObject> createAccountResponseListener = new Response.Listener<JSONObject>() {


        @Override
        public void onResponse(JSONObject response) {
            try {
                apiToken = response.getString("apitoken");
                EncryptionManager encryptionManager = new EncryptionManager();
                try {
                    apiToken = encryptionManager.decrypt(apiToken, getContext());
                } catch (Exception e) {
                    Log.e("encryption", e.getMessage());
                }
            } catch (JSONException e) {
                fail(e.getMessage());
                apiToken = null;
            }

            countDownLatch.countDown();
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse (VolleyError error){
            countDownLatch.countDown();
            fail(error.getMessage());
        }
    };
    private Account account;
    private AccountManager accountManager;
    private SdcServiceLocalImpl sdcService;
    private Response.Listener<String> sessionListFetcherListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String jsonString) {
            Gson gson = new Gson();
            sessionEntries = gson.fromJson(jsonString, SkydivingSessionListEntry[].class);
            countDownLatch.countDown();

        }
    };

    private SkydivingSessionData sessionData;
    private Response.Listener<String> sessionFetcherListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String jsonString) {
            Gson gson = new Gson();
            sessionData = gson.fromJson(jsonString, SkydivingSessionData.class);
            countDownLatch.countDown();

        }
    };

    public void setUp() throws InterruptedException {
        account = getAccount();
        sdcService = new SdcServiceLocalImpl(mContext);
        countDownLatch = new CountDownLatch(1);
        sdcService.createTemporaryAccount(account, createAccountResponseListener, errorListener);
        countDownLatch.await();
    }

    SkydivingSessionListEntry[] sessionEntries;
    public void test_get_list_of_sessions() throws InterruptedException {
        countDownLatch = new CountDownLatch(1);
        sdcService.getSessionList(apiToken, sessionListFetcherListener, errorListener);
        countDownLatch.await();
        assertEquals(1, sessionEntries.length);
        assertEquals(2, sessionEntries[0].getId());
        assertEquals("2016-01-15T17:01:14.047Z", sessionEntries[0].getDate());
    }

    public void test_get_session() throws InterruptedException {
        countDownLatch = new CountDownLatch(1);
        sdcService.getSession(apiToken, 2, sessionFetcherListener, errorListener);
        SkydivingSessionData expectedSession = getSession();
        countDownLatch.await();
        assertEquals(expectedSession.allEntries()[17].getTimestamp(), sessionData.allEntries()[17].getTimestamp());
    }

    private Account getAccount() {
        accountManager = new AccountManager(mContext);
        try {
            return accountManager.getAccount();
        } catch (Exception e) {
            return null;
        }
    }

    private SkydivingSessionData getSession() {
        InputStreamReader jsonReader = new InputStreamReader(
                mContext.getResources().openRawResource(R.raw.sample_log));

        try {
            String jsonString = LogUtils.parse(jsonReader);
            Gson gson = new Gson();
            return gson.fromJson(jsonString, SkydivingSessionData.class);

        } catch (IOException e) {
            fail(e.toString());
        }
        return null;
    }
}
