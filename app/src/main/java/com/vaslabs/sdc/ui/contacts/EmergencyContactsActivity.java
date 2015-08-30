package com.vaslabs.sdc.ui.contacts;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vaslabs.emergency.EmergencyContact;
import com.vaslabs.emergency.EmergencyPreferences;
import com.vaslabs.sdc.ui.R;

import java.util.List;

public class EmergencyContactsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency_contacts, menu);
        EmergencyPreferences ep = EmergencyPreferences.load(this);
        List<EmergencyContact> emergencyContactList = ep.getEmergencyContactList();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_contact) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
