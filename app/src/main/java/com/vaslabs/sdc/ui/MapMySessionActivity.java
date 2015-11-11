package com.vaslabs.sdc.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vaslabs.google.maps.MarkerAnimation;
import com.vaslabs.logbook.SkydivingSessionData;
import com.vaslabs.sdc.entries.Entry;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.animate_session) {
            animateMySession();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void animateMySession() {
        SkydivingSessionData skydivingSessionData = LogbookStats.getLatestSession(this);
        GpsEntry[] gpsEntries = skydivingSessionData.getGpsEntriesAsArray();
        int speedUp = 100;
        GpsEntry gpsEntry = gpsEntries[0];
        LatLng startPosition = new LatLng(gpsEntry.getLatitude(), gpsEntry.getLongitude());

        Entry[] entries = skydivingSessionData.allEntries();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(startPosition)
                .icon(BitmapDescriptorFactory.fromBitmap(MarkerAnimation.getMarkerIcon(this))));
        MarkerAnimation markerAnimation = new MarkerAnimation(entries, marker, speedUp);
        markerAnimation.animateMarkerToGB();
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
        try {
            createPolygons(skydivingSessionData, mMap);
        } catch (Exception e) {
            Toast.makeText(this, "Could not create visualisation from latest skydiving session", Toast.LENGTH_SHORT).show();
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
