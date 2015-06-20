package com.vaslabs.sdc.utils;

/**
 * Created by vnicolao on 20/06/15.
 */
public interface IValidator {
    boolean validate();
    ValidationMessageType getMessageType();

}
