package com.vaslabs.sdc.utils;

import android.content.Context;
import android.widget.AutoCompleteTextView;

/**
 * Created by vnicolaou on 20/12/15.
 */
public class AbstractValidator implements IValidator {
    protected final Context mContext;

    public AbstractValidator(Context c) {
        this.mContext = c;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public ValidationMessageType getMessageType() {
        return null;
    }

    @Override
    public CharSequence getMessage() {
        return null;
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

}
