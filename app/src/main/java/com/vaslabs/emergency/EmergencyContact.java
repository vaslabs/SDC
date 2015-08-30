package com.vaslabs.emergency;

import com.google.gson.annotations.Expose;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class EmergencyContact {

    @Expose
    protected final String name;

    @Expose
    protected final String phoneNumber;

    public EmergencyContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
