package com.vaslabs.accounts;

/**
 * Created by vnicolaou on 15/01/16.
 */
public class AccountExistsException extends RuntimeException{

    public AccountExistsException(Throwable t) {
        super(t);
    }
}
