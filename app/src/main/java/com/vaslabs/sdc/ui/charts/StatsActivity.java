package com.vaslabs.sdc.ui.charts;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vaslabs.accounts.Account;
import com.vaslabs.accounts.AccountManager;
import com.vaslabs.sdc.connectivity.SdcService;
import com.vaslabs.sdc.ui.Main2Activity;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.ui.fragments.actions.ActionManager;
import com.vaslabs.sdc.ui.fragments.actions.DefaultActionManager;

import java.util.HashMap;
import java.util.Map;

public class StatsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, ActionManager> actionManagerHolder = new HashMap<Integer, ActionManager>();
    private String apiToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initActions();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_stats);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionManagerHolder.get(R.id.nav_altitude).manageAction(this);

    }

    private void initActions() {
        actionManagerHolder.put(R.id.nav_altitude, new DefaultActionManager(BarometerChartFragment.class) {
        });
        actionManagerHolder.put(R.id.nav_velocity, new DefaultActionManager(VelocityChartFragment.class) {
                });
        actionManagerHolder.put(R.id.nav_acceleration, new DefaultActionManager(AccelerationChartFragment.class) {
        });
        actionManagerHolder.put(R.id.nav_gforce, new DefaultActionManager(GforceChartFragment.class) {
        });
        actionManagerHolder.put(R.id.nav_map, new MapActionManager());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_stats);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stats, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ActionManager actionManager = actionManagerHolder.get(id);
        if (actionManager == null) {
            Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
            return true;
        }
        actionManager.manageAction(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_stats);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
