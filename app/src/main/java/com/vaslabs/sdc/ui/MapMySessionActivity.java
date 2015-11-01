package com.vaslabs.sdc.ui;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.GpsEntries;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.types.SkydivingEvent;
import com.vaslabs.sdc.types.SkydivingEventDetails;
import com.vaslabs.sdc.ui.R;

import java.util.ArrayList;
import java.util.List;

public class MapMySessionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_my_session);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(this);
        createPolygons(skydivingSessionData, mMap);
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
                    .strokeColor(skydivingEventDetails[2].eventType.color));
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
