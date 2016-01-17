package com.vaslabs.accounts;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc_dashboard.API.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;


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
        }
    };


    public void setUp() throws Exception {
        sdcService = new SdcServiceLocalImpl(mContext);
        accountManager = new AccountManager(mContext);
        API.deleteToken(mContext);
        account = accountManager.getAccount();
        countDownLatch = new CountDownLatch(1);
        sdcService.createTemporaryAccount(account, createAccountResponseListener, errorListener);
        countDownLatch.await();
    }

    public void test_create_temporary_account_duplicate_gets_same_token() throws Exception {
        Account account = accountManager.getAccount();
        assertTrue(account instanceof TemporaryAccount);
        assertEquals(this.account, account);
        countDownLatch = new CountDownLatch(1);
        String previousApitoken = apiToken;
        sdcService.createTemporaryAccount(accountManager.getAccount(), createAccountResponseListener,
                errorListener);
        countDownLatch.await();
        assertEquals(previousApitoken, this.apiToken);
    }

}
