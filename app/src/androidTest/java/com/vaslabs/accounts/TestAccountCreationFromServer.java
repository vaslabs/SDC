package com.vaslabs.accounts;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.connectivity.impl.SdcServiceImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.ErrorListener;

/**
 * Created by vnicolaou on 14/01/16.
 */
public class TestAccountCreationFromServer extends AndroidTestCase {
    SdcService sdcService;
    private String apiToken;
    private AccountManager accountManager;
    private Account account;
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


    public void setUp() throws Exception {
        sdcService = new SdcServiceLocalImpl();
        accountManager = new AccountManager(mContext);
        account = accountManager.getAccount();
        countDownLatch = new CountDownLatch(1);
        sdcService.createTemporaryAccount(account, createAccountResponseListener, errorListener);
        countDownLatch.await();
        assertEquals(32, apiToken.length());
    }

    public void test_create_temporary_account_duplicate_gets_same_token() throws Exception {
        Account account = accountManager.getAccount();
        assertEquals(this.account, account);
        countDownLatch = new CountDownLatch(1);
        String previousApitoken = apiToken;
        sdcService.createTemporaryAccount(accountManager.getAccount(), createAccountResponseListener,
                errorListener);
        countDownLatch.await();
        assertEquals(previousApitoken, this.apiToken);
    }

    public void test_get_number_of_sessions_from_server() {
        int numberOfSessions = sdcService.getNumberOfSessions(apiToken);
        assertEquals(0, numberOfSessions);
    }

    private class SdcServiceLocalImpl extends SdcServiceImpl {
        public SdcServiceLocalImpl() {
            super("10.0.2.2:8000", "http://", getContext());
        }
    }
}
