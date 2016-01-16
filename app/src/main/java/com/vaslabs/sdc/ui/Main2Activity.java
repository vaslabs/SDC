package com.vaslabs.sdc.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.sdc.SkydivingSessionService;
import com.vaslabs.sdc.ui.fragments.CardViewFragment;
import com.vaslabs.sdc.ui.fragments.ManageLogsFragment;
import com.vaslabs.sdc.ui.fragments.actions.ActionManager;
import com.vaslabs.sdc.ui.fragments.actions.LogbookSummaryActionManager;
import com.vaslabs.sdc.ui.fragments.actions.LogsSubmissionActionManager;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CardViewFragment.OnListFragmentInteractionListener,
        ManageLogsFragment.OnFragmentInteractionListener
{

    private Map<Integer, ActionManager> actionManagerHolder = new HashMap<Integer, ActionManager>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        processIntent(intent);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        actionManagerHolder.put(R.id.nav_logbook, new LogbookSummaryActionManager());
        actionManagerHolder.put(R.id.nav_api, new QRApiScannerActionManager());
        actionManagerHolder.put(R.id.nav_logsmanagement, new LogsSubmissionActionManager());
        actionManagerHolder.put(R.id.nav_about, new AboutActionManager());
        actionManagerHolder.put(R.id.nav_stats, new StatsActionManager());
        start();
    }

    private void start() {
        actionManagerHolder.get(R.id.nav_logbook).manageAction(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(LogbookSummary logbookSummary) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case SkydivingSessionService.CLOSE_ACTION:
                exit();
                break;
        }
    }

    private void exit() {
        stopService(new Intent(this, SkydivingSessionService.class));
        finish();
    }
}
