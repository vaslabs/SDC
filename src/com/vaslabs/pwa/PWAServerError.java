package com.vaslabs.pwa;

public class PWAServerError extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5777117934764239853L;

    public String getMessage() {
        return "Server error";
    }
}
