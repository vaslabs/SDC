package com.vaslabs.accounts;

import android.content.Context;

import com.vaslabs.encryption.EncryptionManager;

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
        return new SDCAccount();
    }

    public boolean isLoggedIn() {
        return false;
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
