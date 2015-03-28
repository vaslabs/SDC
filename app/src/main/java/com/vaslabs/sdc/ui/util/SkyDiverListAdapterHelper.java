package com.vaslabs.sdc.ui.util;

import android.graphics.Color;

import com.vaslabs.sdc.utils.SDConnectivity;


public class SkyDiverListAdapterHelper {

    

    public static int[] getColors() {
        int[] colors = new int[SDConnectivity.values().length];
        colors[SDConnectivity.CONNECTION_LOST.ordinal()] = Color.GRAY;
        colors[SDConnectivity.WEAK.ordinal()] = Color.YELLOW;
        colors[SDConnectivity.MEDIUM.ordinal()] = Color.MAGENTA;
        colors[SDConnectivity.STRONG.ordinal()] = Color.RED;
        return colors;
    }

    public static int getDefaultColor() {
        return Color.WHITE;
    }

}
