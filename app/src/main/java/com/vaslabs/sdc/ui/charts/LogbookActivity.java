package com.vaslabs.sdc.ui.charts;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vaslabs.sdc.entries.BarometerEntries;
import com.vaslabs.sdc.logs.LogbookStats;
import com.vaslabs.sdc.ui.R;

import java.io.IOException;
import java.io.InputStreamReader;

public class LogbookActivity extends Activity {

    private LogbookStats stats;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook);

        Gson gson = new Gson();
        InputStreamReader jsonReader = new InputStreamReader(
                this.getResources().openRawResource(R.raw.barometer_test_data));
        BarometerEntries barometerEntries = gson.fromJson(jsonReader, BarometerEntries.class);
        try {
            jsonReader.close();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        stats = LogbookStats.generateLogbookStats(barometerEntries);
        textView = (TextView)findViewById(R.id.logTextView);
        textView.setText(stats.toString());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
