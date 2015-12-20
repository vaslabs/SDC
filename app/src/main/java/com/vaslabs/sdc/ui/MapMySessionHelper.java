package com.vaslabs.sdc.ui;

import android.content.Context;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vaslabs.google.maps.MarkerAnimation;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GpsEntries;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.types.SkydivingEvent;
import com.vaslabs.sdc.types.SkydivingEventDetails;

import java.util.ArrayList;
import java.util.List;

public class MapMySessionHelper {



    private static void animateMySession(Context c, GoogleMap mMap) {
        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(c);
        GpsEntry[] gpsEntries = skydivingSessionData.getGpsEntriesAsArray();
        int speedUp = 100;
        GpsEntry gpsEntry = gpsEntries[0];
        LatLng startPosition = new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude());

        Entry[] entries = skydivingSessionData.allEntries();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(MarkerAnimation.getMarkerIcon(c))));
        MarkerAnimation markerAnimation = new MarkerAnimation(entries, marker, speedUp);
        markerAnimation.animateMarkerToGB();
    }

    public static void onMapReady(GoogleMap mMap, Context context) {

        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(context);
        try {
            createPolygons(skydivingSessionData, mMap);
        } catch (Exception e) {
            Toast.makeText(context, "Could not create visualisation from latest skydiving session", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            GpsEntry gpsEntry = skydivingSessionData.getGpsEntries().getEntry(0);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude()), 14f));
        } catch (Exception e) {

        }

    }

    private static void createPolygons(SkydivingSessionData skydivingSessionData, GoogleMap map) {
        SkydivingEventDetails[] skydivingEventDetails = LogbookStats.identifyFlyingEvents(skydivingSessionData.getBarometerEntries());
        LatLng[] latLngArray = generatePath(skydivingSessionData, 0, skydivingEventDetails[0].timestamp);
        if (latLngArray.length > 0) {
            map.addPolygon(new PolygonOptions()
                    .add(latLngArray)
                    .strokeColor(SkydivingEvent.WALKING.color));
        }
        latLngArray = generatePath(skydivingSessionData, skydivingEventDetails[1].timestamp, skydivingEventDetails[2].timestamp);
        if (latLngArray.length > 0) {
            map.addPolygon(new PolygonOptions()
                    .add(latLngArray)
                    .strokeColor(skydivingEventDetails[1].eventType.color));
        }
        latLngArray = generatePath(skydivingSessionData, skydivingEventDetails[2].timestamp, skydivingEventDetails[3].timestamp);
        if (latLngArray.length > 0) {
            map.addPolygon(new PolygonOptions()
                    .add(latLngArray)
                    .strokeColor(skydivingEventDetails[2].eventType.color));
        }

        GpsEntries gpsEntries = skydivingSessionData.getGpsEntries();
        latLngArray = generatePath(skydivingSessionData, skydivingEventDetails[3].timestamp, gpsEntries.getEntry(gpsEntries.size() - 1).getTimestamp());
        if (latLngArray.length > 0) {
            map.addPolygon(new PolygonOptions()
                    .add(latLngArray)
                    .strokeColor(SkydivingEvent.WALKING.color));
        }
    }

    private static LatLng[] generatePath(SkydivingSessionData skydivingSessionData, long timestampLeft, long timestampRight ) {
        GpsEntries gpsEntries = skydivingSessionData.getGpsEntries();
        gpsEntries.sort();
        List<LatLng> latLngList = new ArrayList<LatLng>();
        for (int i = 0; i < gpsEntries.size(); i++) {
            if (gpsEntries.getEntry(i).getTimestamp() > timestampRight)
                break;
            if (gpsEntries.getEntry(i).getTimestamp() < timestampLeft)
                continue;
            latLngList.add(new LatLng(gpsEntries.getEntry(i).getLatitude(), gpsEntries.getEntry(i).getLongitude()));
        }
        LatLng[] latLngArray = new LatLng[latLngList.size()];
        if (latLngList.size() == 0) {
            return latLngArray;
        }
        return latLngList.toArray(latLngArray);
    }
}
