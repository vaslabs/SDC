package com.vaslabs.sdc.types;

import android.graphics.Color;

/**
 * Created by vnicolaou on 01/11/15.
 */
public enum SkydivingEvent {
    TAKE_OFF(Color.BLUE), FREE_FALL(Color.RED), CANOPY(Color.GREEN), LANDING(Color.BLACK), WALKING(Color.BLACK);
    public final int color;

    SkydivingEvent(int color) {
        this.color = color;
    }
}
