package com.vaslabs.sdc.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class WifiValidator extends AbstractValidator {

    private static WifiValidator wifiValidator = null;
    private static final Object initLock = new Object();
    private String message = "Wifi is enabled";
    protected WifiValidator(Context c) {
        super(c);
    }

    @Override
    public boolean validate() {
        WifiManager wifi = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifi == null)
            return false;
        return wifi.isWifiEnabled();
    }



    @Override
    public ValidationMessageType getMessageType() {
        return ValidationMessageType.WARNING;
    }

    @Override
    public CharSequence getMessage() {
        return message;
    }

    @Override
    public CharSequence getTitle() {
        return "Wifi";
    }

    public static IValidator getInstance(Context mContext) {
        synchronized (initLock) {
            if (wifiValidator == null)
                wifiValidator = new WifiValidator(mContext);
        }
        return wifiValidator;
    }
}
