package com.vaslabs.sdc.utils;


import com.vaslabs.sdc.types.ISummaryEntry;

/**
 * Created by vnicolao on 20/06/15.
 */
public interface IValidator {
    boolean validate();
    ValidationMessageType getMessageType();
    CharSequence getMessage();
    CharSequence getTitle();
}
