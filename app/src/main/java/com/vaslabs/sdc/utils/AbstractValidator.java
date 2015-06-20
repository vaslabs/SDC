package com.vaslabs.sdc.utils;

import android.content.Context;

/**
 * Created by vnicolao on 20/06/15.
 */
public abstract class AbstractValidator implements IValidator {
    protected final Context mContext;
    protected AbstractValidator(Context c) {
        this.mContext = c;
    }


}
