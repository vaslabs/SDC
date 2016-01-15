package com.vaslabs.accounts;

import android.util.Base64;

import com.vaslabs.encryption.EncryptionManager;

import java.security.PublicKey;

/**
 * Created by vnicolaou on 12/01/16.
 */
public class TemporaryAccount extends Account {
    private final PublicKey publicKey;

    public TemporaryAccount(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String getKey() {
        return publicKeyToString(publicKey);
    }

    private String publicKeyToString(PublicKey publicKey) {
        return EncryptionManager.encodePublicKey(publicKey);
    }

}
