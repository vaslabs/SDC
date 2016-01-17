package com.vaslabs.accounts;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.logs.utils.LogUtils;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.connectivity.impl.SdcServiceImpl;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

/**
 * Created by vnicolaou on 15/01/16.
 */
public class TestTemporaryAccountSubmission extends AndroidTestCase {

    private AccountManager accountManager;
    private Account account;
    private SdcService sdcService;
    private String session;
    private CountDownLatch countDownLatch;
    private String apiToken;

    private String message;

    Response.Listener<JSONObject> submissionListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            try {
                message = response.getString("message");
            } catch (JSONException e) {
                Log.e("Message", e.getMessage());
            }
            countDownLatch.countDown();
        }
    };

    Response.Listener<JSONObject> createAccountResponseListener = new Response.Listener<JSONObject>() {


        @Override
        public void onResponse(JSONObject response) {
            try {
                apiToken = response.getString("apitoken");
                EncryptionManager encryptionManager = new EncryptionManager();
                try {
                    apiToken = encryptionManager.decrypt(apiToken, getContext());
                    API.saveApiToken(getContext(), apiToken);
                } catch (Exception e) {
                    Log.e("encryption", e.getMessage());
                }
            } catch (JSONException e) {
                apiToken = null;
            }

            countDownLatch.countDown();
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse (VolleyError error){
            countDownLatch.countDown();
            Log.e("Volley", error.getMessage());
        }
    };

    public void setUp() throws InterruptedException {
        account = getAccount();
        sdcService = new SdcServiceLocalImpl(mContext);
        if (account instanceof TemporaryAccount) {
            countDownLatch = new CountDownLatch(1);
            sdcService.createTemporaryAccount(account, createAccountResponseListener, errorListener);
            countDownLatch.await();
        }
    }

    public void test_temporary_account_submission() throws InterruptedException {
        countDownLatch = new CountDownLatch(1);
        sdcService.submitSession(apiToken, getSession(), submissionListener, errorListener);
        countDownLatch.await();
        assertEquals("OK", message);
    }

    private Account getAccount() {
        accountManager = new AccountManager(mContext);
        try {
            return accountManager.getAccount();
        } catch (Exception e) {
            return null;
        }
    }

    private String getSession() {
        InputStreamReader jsonReader = new InputStreamReader(
                mContext.getResources().openRawResource(R.raw.sample_log));

        try {
            String jsonString = LogUtils.parse(jsonReader);
            return jsonString;

        } catch (IOException e) {
            fail(e.toString());
        }
        return null;
    }
}
