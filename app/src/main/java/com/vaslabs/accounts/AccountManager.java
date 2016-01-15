package com.vaslabs.accounts;

import android.content.Context;

import com.vaslabs.encryption.EncryptionManager;
import com.vaslabs.sdc_dashboard.API.API;

import java.io.IOException;

/**
 * Created by vnicolaou on 12/01/16.
 */
public class AccountManager {
    private final Context mContext;

    public AccountManager(Context mContext) {
        this.mContext = mContext;
    }

    public void deleteTemporaryAccount() {
        mContext.getFileStreamPath("rsa").delete();
        mContext.getFileStreamPath("rsa.pub").delete();
    }

    public Account getAccount() throws Exception {
        if (!isLoggedIn())
            return getTemporaryAccount();
        return new SDCAccount(API.getApiToken(mContext));
    }

    public boolean isLoggedIn() {
        return false;/*
        String token = null;
        try {
            token = API.getApiToken(mContext);
        } catch (IOException e) {
            return false;
        }
        return token != null;*/

    }


    private TemporaryAccount getTemporaryAccount() throws Exception {
        EncryptionManager em = new EncryptionManager();
        try {
            em.getPublicKey(mContext);
        } catch (IOException e) {
            em.generateKeys(mContext);
        }
        return new TemporaryAccount(em.getPublicKey(mContext));
    }
}
