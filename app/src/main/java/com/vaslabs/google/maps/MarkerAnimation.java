/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */

package com.vaslabs.google.maps;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.vaslabs.sdc.entries.GpsEntry;
import com.google.maps.android.SphericalUtil;
import java.util.Arrays;

/**
 * Enhanced by
 * @author vnicolaou
 */
public class MarkerAnimation {

    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
    final Marker marker;
    final GpsEntry[] entries;
    final int speedUp;
    public MarkerAnimation(GpsEntry[] entries, Marker marker, int speedUp) {
        this.marker = marker;
        this.entries = Arrays.copyOf(entries, entries.length);
        this.speedUp = speedUp;
        boolean initialised = false;
        GpsEntry gpsEntry = entries[0];
        LatLng startPosition = new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude());
        marker.setPosition(startPosition);
    }

    public void animateMarkerToGB() {
        animateMarkerToGB(null);
    }

    public void animateMarkerToGB(LatLngInterpolator positionInterpolator) {
        final LatLngInterpolator latLngInterpolator;
        if (positionInterpolator == null) {
            latLngInterpolator = new LatLngInterpolator.Linear();
        } else {
            latLngInterpolator = positionInterpolator;
        }
        handler.post(new Runnable() {
            float t;
            float v;
            int entryIndex = 1;
            @Override
            public void run() {
                final long durationInMs = (entries[entryIndex].getTimestamp() - entries[entryIndex - 1].getTimestamp()) / speedUp;
                t = (durationInMs);
                LatLng startPosition = new LatLng(entries[entryIndex - 1].getLatitude(), entries[entryIndex - 1].getLongitude());
                LatLng finalPosition = new LatLng(entries[entryIndex].getLatitude(), entries[entryIndex].getLongitude());
                marker.setPosition(latLngInterpolator.interpolate(1, startPosition, finalPosition));
                marker.setRotation((float) SphericalUtil.computeHeading(startPosition, finalPosition));
                entryIndex++;
                if (entryIndex < entries.length - 1) {
                    handler.postDelayed(this, durationInMs);
                }

            }
        });
    }
}