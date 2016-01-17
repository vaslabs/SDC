package com.vaslabs.accounts;

import com.vaslabs.encryption.EncryptionManager;

import java.util.BitSet;


/**
 * Created by vnicolaou on 12/01/16.
 */
public abstract class Account {

    public abstract String getKey();

    @Override
    public boolean equals(Object object) {
        if (object instanceof Account)
            return getKey().equals(((Account) object).getKey());
        return false;
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
