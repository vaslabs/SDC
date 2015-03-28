package com.vaslabs.sdc.pwa;

public class PWAInvalidCredentialsException extends Exception {
    

    /**
     * 
     */
    private static final long serialVersionUID = 8545798696133021622L;

    public PWAInvalidCredentialsException() {
        
    }
    
    @Override
    public String getMessage() {
        return "Wrong username or password";
    }
}
