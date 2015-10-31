package com.vaslabs.sdc.entries;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class ConnectionEntry extends Entry{

    private String deviceName;
    private int connectionEvent;

    public ConnectionEntry(long newTimeStamTimestamp) {
        super(newTimeStamTimestamp);
    }

    public int getConnectionEvent() {
        return connectionEvent;
    }

    public String getDeviceName() {
        return deviceName;
    }


    @Override
    public Entry withTimestamp(long newTimeStamTimestamp) {
        if (newTimeStamTimestamp == this.getTimestamp())
            return this;
        ConnectionEntry ce = new ConnectionEntry(newTimeStamTimestamp);
        ce.deviceName = this.deviceName;
        ce.connectionEvent = this.connectionEvent;
        return ce;
    }

    @Override
    public float getX() {
        return getTimestamp()/1000f;
    }

    @Override
    public float getY() {
        return connectionEvent;
    }
}
