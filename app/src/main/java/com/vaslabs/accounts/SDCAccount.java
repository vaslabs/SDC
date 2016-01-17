package com.vaslabs.accounts;

/**
 * Created by vnicolaou on 12/01/16.
 */
public class SDCAccount extends Account {

    private final String key;

    public SDCAccount(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {

        return this.key;
    }
}
