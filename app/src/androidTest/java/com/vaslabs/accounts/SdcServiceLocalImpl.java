package com.vaslabs.accounts;

import android.content.Context;

import com.vaslabs.sdc.connectivity.impl.SdcServiceImpl;
import com.vaslabs.sdc.ui.R;

public class SdcServiceLocalImpl extends SdcServiceImpl {
        public SdcServiceLocalImpl(Context context) {
            super(context.getString(R.string.remote_host), context);
        }
}