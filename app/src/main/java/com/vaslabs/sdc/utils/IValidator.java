package com.vaslabs.sdc.utils;

import com.dexafree.materialList.cards.SmallImageCard;

/**
 * Created by vnicolao on 20/06/15.
 */
public interface IValidator {
    boolean validate();
    ValidationMessageType getMessageType();
    CharSequence getMessage();
    CharSequence getTitle();

    void attachCard(SmallImageCard card);

    void refreshImage(boolean valid);
}
