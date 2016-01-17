package com.vaslabs.accounts;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by vnicolaou on 15/01/16.
 */
public class TestPublicKey extends AndroidTestCase{
    private String apiToken;
    private AccountManager accountManager;
    private Account account;
    public void setUp() throws Exception {
        accountManager = new AccountManager(mContext);
        account = accountManager.getAccount();
        Log.i("PublicKey", account.getKey());

    }

    public void test_account() {
        assertNotNull(account);
    }
}
