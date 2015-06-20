package com.vaslabs.sdc.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class LocationValidator extends AbstractValidator{


    private static final Object initLock = new Object();
    private static LocationValidator locationValidator = null;

    protected LocationValidator(Context mContext) {
        super(mContext);
    }

    @Override
    public boolean validate() {
        LocationManager lm =
                (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);

        if (lm == null)
            return false;
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public ValidationMessageType getMessageType() {
        return ValidationMessageType.ERROR;
    }

    @Override
    public CharSequence getMessage() {
        return "GPS is enabled";
    }

    @Override
    public CharSequence getTitle() {
        return "GPS Location";
    }

    public static IValidator getInstance(Context mContext) {
        synchronized (initLock) {
            if (locationValidator == null) {
                locationValidator = new LocationValidator(mContext);
            }
        }
        return locationValidator;
    }
}
