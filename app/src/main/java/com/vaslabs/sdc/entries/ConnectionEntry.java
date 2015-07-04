package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class ConnectionEntry extends Entry{

    private String deviceName;
    private int connectionEvent;

    public int getConnectionEvent() {
        return connectionEvent;
    }

    public String getDeviceName() {
        return deviceName;
    }



}
