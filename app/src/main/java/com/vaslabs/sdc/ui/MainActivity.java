package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vaslabs.sdc.ui.charts.BarometerChartActivity;
import com.vaslabs.sdc.ui.charts.LogbookActivity;

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
        Intent intent;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity( intent );
                return true;
            case R.id.action_charts:
                intent = new Intent(this, BarometerChartActivity.class);
                this.startActivity( intent );
                return true;
            case R.id.logbook:
                intent = new Intent(this, LogbookActivity.class);
                this.startActivity( intent );
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button skyDivingSessionButton;
        private Button manageLogsButton;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState ) {
            View rootView =
                    inflater.inflate( R.layout.fragment_main, container, false );
            
            skyDivingSessionButton = (Button) rootView.findViewById( R.id.skyDivingSessionButton );
            manageLogsButton = (Button)rootView.findViewById( R.id.manageLogsButton );
            
            skyDivingSessionButton.setOnClickListener( new View.OnClickListener() {
                
                @Override
                public void onClick( View v ) {
                    Intent intent = new Intent(v.getContext(), ValidationActivity.class);
                    startActivity( intent );
                }
            } );
            
            manageLogsButton.setOnClickListener( new View.OnClickListener() {
                
                @Override
                public void onClick( View v ) {
                    Intent intent = new Intent(v.getContext(), ManageLogsActivity.class);
                    startActivity(intent);
                }
            });
            
            return rootView;
        }
    }
}
