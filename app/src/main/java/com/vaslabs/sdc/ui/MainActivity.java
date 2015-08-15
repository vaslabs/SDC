package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vaslabs.sdc.ui.charts.BarometerChartActivity;
import com.vaslabs.sdc.ui.charts.LogbookActivity;
import com.vaslabs.sdc.ui.charts.VelocityChartActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        if ( savedInstanceState == null ) {
            getFragmentManager().beginTransaction()
                    .add( R.id.container, new PlaceholderFragment() ).commit();
        }
        
        
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.barometer_chart:
                intent = new Intent(this, BarometerChartActivity.class);
            case R.id.velocity_chart:
                intent = new Intent(this, VelocityChartActivity.class);
                break;
            case R.id.logbook:
                intent = new Intent(this, LogbookActivity.class);
                break;
            case R.id.submitlogs:
                intent = new Intent(this, ManageLogsActivity.class);
                break;
            case R.id.apisetup:
                intent = new Intent(this, QRApiScannerActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ImageButton skyDivingSessionButton;
        private ShimmerTextView shimmerTextView;
        Shimmer shimmer;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState ) {
            View rootView =
                    inflater.inflate( R.layout.fragment_main, container, false );
            
            skyDivingSessionButton = (ImageButton) rootView.findViewById( R.id.skyDivingSessionButton );
            skyDivingSessionButton.setBackgroundColor(Color.TRANSPARENT);
            skyDivingSessionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    shimmer.cancel();
                    Intent intent = new Intent(v.getContext(), ValidationActivity.class);
                    startActivity(intent);
                }
            });

            shimmerTextView = (ShimmerTextView) rootView.findViewById(R.id.shimmer_tv);
            shimmerTextView.bringToFront();
            shimmer = new Shimmer();
            shimmer.start(shimmerTextView);

            
            return rootView;
        }
    }
}
