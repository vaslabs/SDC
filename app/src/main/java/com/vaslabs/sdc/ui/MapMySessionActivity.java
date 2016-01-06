package com.vaslabs.sdc.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vaslabs.google.maps.MarkerAnimation;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.Entry;
import com.vaslabs.sdc.entries.GpsEntry;
import com.vaslabs.sdc.logs.LogbookStats;

/**
 * Created by vnicolaou on 06/01/16.
 */
public class MapMySessionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_my_session);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab = (FloatingActionButton) findViewById(R.id.fab_play);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null)
                    animateMySession();
            }
        });
    }


    private void animateMySession() {
        this.fab.hide();
        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(this);
        GpsEntry[] gpsEntries = skydivingSessionData.getGpsEntriesAsArray();
        int speedUp = 1000;
        GpsEntry gpsEntry = gpsEntries[0];
        LatLng startPosition = new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude());

        Entry[] entries = skydivingSessionData.allEntries();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(MarkerAnimation.getMarkerIcon(this))));
        MarkerAnimation markerAnimation = new MarkerAnimation(entries, marker, speedUp);
        markerAnimation.animateMarkerToGB();
        this.fab.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MapMySessionHelper.onMapReady(mMap, this);
    }
}
