package com.vaslabs.accounts;

import android.content.Context;

import com.vaslabs.sdc.connectivity.impl.SdcServiceImpl;

public class SdcServiceLocalImpl extends SdcServiceImpl {
        public SdcServiceLocalImpl(Context context) {
            super("10.0.2.2:8000", "http://", context);
        }
}