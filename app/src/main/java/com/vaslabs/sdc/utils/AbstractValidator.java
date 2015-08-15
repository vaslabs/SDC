package com.vaslabs.sdc.utils;

import android.content.Context;

import com.dexafree.materialList.cards.SmallImageCard;
import com.vaslabs.sdc.ui.R;

/**
 * Created by vnicolao on 20/06/15.
 */
public abstract class AbstractValidator implements IValidator {
    protected final Context mContext;
    private SmallImageCard card;

    protected AbstractValidator(Context c) {
        this.mContext = c;
    }

    @Override
    public void attachCard(SmallImageCard card) {
        this.card = card;
    }

    @Override
    public void refreshImage(boolean valid) {
        int drawable = R.drawable.ic_success;
        if (!valid) {
            drawable = getResource(this.getMessageType());
            this.card.setDrawable(drawable);
        }

    }

    private static int getResource(ValidationMessageType ordinal) {
        switch (ordinal) {
            case WARNING:
                return R.drawable.ic_warning;
            case ERROR:
                return R.drawable.ic_error;
            default:
                return R.drawable.ic_success;
        }
    }
}
