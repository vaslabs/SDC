package com.vaslabs.sdc.utils;

import android.content.Context;

import com.vaslabs.emergency.EmergencyPreferences;
import com.vaslabs.sdc.ui.R;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class EmergencyContactValidator extends AbstractValidator {
    private static EmergencyContactValidator validator = null;
    private static Object initLock = new Object();
    protected EmergencyContactValidator(Context c) {
        super(c);
    }

    @Override
    public boolean validate() {
        EmergencyPreferences ep = EmergencyPreferences.load(this.mContext);
        return ep.getEmergencyContactList() != null && ep.getEmergencyContactList().size() > 0;
    }

    @Override
    public ValidationMessageType getMessageType() {
        return ValidationMessageType.WARNING;
    }

    @Override
    public CharSequence getMessage() {
        return this.mContext.getString(R.string.emergency_contact_missing);
    }

    @Override
    public CharSequence getTitle() {
        return this.mContext.getString(R.string.emergency_contact_title);
    }

    public static IValidator getInstance(Context mContext) {
        if (validator != null)
            return validator;
        synchronized (initLock) {
            if (validator != null)
                return validator;
            EmergencyContactValidator evalidator = new EmergencyContactValidator(mContext);
            validator = evalidator;
        }
        return validator;
    }

}
