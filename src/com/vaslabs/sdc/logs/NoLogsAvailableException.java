package com.vaslabs.sdc.logs;

public class NoLogsAvailableException extends Exception {

    
    protected NoLogsAvailableException() {
        
    }
    
    public String getMessage() {
        return "No logs available";
    }
    
}
