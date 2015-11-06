/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */

package com.vaslabs.google.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.vaslabs.sdc.entries.BarometerEntry;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GpsEntry;
import com.google.maps.android.SphericalUtil;
import com.vaslabs.sdc.ui.R;

import java.util.Arrays;

/**
 * Enhanced by
 * @author vnicolaou
 */
public class MarkerAnimation {

    public static final int ICON_MIN = 16;
    public static final int ICON_MAX = 128;;
    public static Bitmap baseBitmap;

    public static Bitmap getMarkerIcon(int iconMin, Context context) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
        baseBitmap = imageBitmap;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, MarkerAnimation.ICON_MIN, MarkerAnimation.ICON_MIN, false);
        return resizedBitmap;
    }

    public static Bitmap getMarkerIcon(Context context) {
        return getMarkerIcon(ICON_MIN, context);
    }

    private static Bitmap getMarkerIcon(int size) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(baseBitmap, size, size, false);
        return resizedBitmap;
    }



    final Handler handler = new Handler();
    final long start = SystemClock.uptimeMillis();
    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
    final Marker marker;
    final Entry[] entries;
    final int speedUp;
    final float baseAltitudeMin;
    final float baseAltitudeMax;
    public MarkerAnimation(Entry[] entries, Marker marker, int speedUp) {
        this.marker = marker;
        this.entries = Arrays.copyOf(entries, entries.length);
        this.speedUp = speedUp;
        float baseAltitudeMin = 0;
        float baseAltitudeMax = 0;
        for (Entry entry : entries) {
            if (entry instanceof BarometerEntry) {
                final float baseAltitude = ((BarometerEntry)entry).getAltitude();
                if (baseAltitude > baseAltitudeMax)
                    baseAltitudeMax = baseAltitude;
                else if (baseAltitude < baseAltitudeMin) {
                    baseAltitudeMin = baseAltitude;
                }
            }
        }
        this.baseAltitudeMin = baseAltitudeMin;
        this.baseAltitudeMax = baseAltitudeMax;
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
                Entry entry = entries[entryIndex];
                final long durationInMs = (entry.getTimestamp() - entries[entryIndex-1].getTimestamp()) / speedUp;
                t = (durationInMs);
                if (entry instanceof GpsEntry) {
                    final GpsEntry gpsEntry = (GpsEntry)entry;
                    LatLng lastPosition = marker.getPosition();
                    LatLng startPosition = lastPosition;
                    LatLng finalPosition = new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude());
                    marker.setPosition(latLngInterpolator.interpolate(1, startPosition, finalPosition));
                    marker.setRotation((float) SphericalUtil.computeHeading(startPosition, finalPosition));
                } else if (entry instanceof BarometerEntry) {
                    final BarometerEntry barometerEntry = (BarometerEntry)entry;
                    final float portion = (barometerEntry.getAltitude() - baseAltitudeMin)/(baseAltitudeMax-baseAltitudeMin);
                    final float size = portion*(ICON_MAX-ICON_MIN) + ICON_MIN;
                    final Bitmap newIcon = getMarkerIcon((int)size);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(newIcon));
                }
                entryIndex++;
                if (entryIndex < entries.length - 1) {
                    handler.postDelayed(this, durationInMs);
                }

            }
        });
    }
}