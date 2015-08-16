package com.vaslabs.sdc.ui.charts;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.vaslabs.logbook.LogbookAPI;
import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.units.CompositeUnit;
import com.vaslabs.sdc.units.DistanceUnit;
import com.vaslabs.sdc.units.TimeUnit;

import java.util.Calendar;

public class LogbookSummaryActivity extends Activity {

    MaterialListView logbookSummaryListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_summary);

        logbookSummaryListView = (MaterialListView) findViewById(R.id.logbookSummaryListView);
        LogbookSummary logbookSummary = LogbookAPI.MOCK.getLogbookSummary(null, this);

        Card[] viewCards = toCards(logbookSummary, DistanceUnit.FEET, DistanceUnit.KM, TimeUnit.HOURS);

        for (Card card : viewCards) {
            logbookSummaryListView.add(card);
        }

    }

    private Card[] toCards(LogbookSummary logbookSummary, DistanceUnit distancePref, TimeUnit timePref) {
        return toCards(logbookSummary, distancePref, null, timePref);
    }

    private Card[] toCards(LogbookSummary logbookSummary, DistanceUnit metricPreference, DistanceUnit speedPreference, TimeUnit timeUnitPreference) {
        if (speedPreference == null)
            speedPreference = metricPreference;
        int averageDeployAltitude = logbookSummary.getAverageDeployAltitude();
        double averageDeployAltitudeMetric = metricPreference.convert(DistanceUnit.METERS,
                averageDeployAltitude);

        int averageExitAltitude = logbookSummary.getAverageExitAltitude();
        double averageExitAltitudeMetric = metricPreference.convert(DistanceUnit.METERS,
                averageExitAltitude);

        double averageSpeed = logbookSummary.getAverageSpeed();

        CompositeUnit<DistanceUnit, TimeUnit> compositeUnitAverageSpeed =
                new CompositeUnit<>(DistanceUnit.METERS, TimeUnit.SECONDS, averageSpeed);

        compositeUnitAverageSpeed = compositeUnitAverageSpeed.convert(speedPreference,
                timeUnitPreference);


        double averageMaxSpeed = logbookSummary.getAverageTopSpeed();
        CompositeUnit<DistanceUnit, TimeUnit> compositeUnitAverageMaxSpeed =
                new CompositeUnit<>(DistanceUnit.METERS, TimeUnit.SECONDS, averageMaxSpeed);

        compositeUnitAverageMaxSpeed = compositeUnitAverageMaxSpeed.convert(speedPreference,
                timeUnitPreference);

        int numberOfDives = logbookSummary.getNumberOfJumps();
        long latestJumpDate = logbookSummary.getLatestJumpDate();


        Card[] cards = new Card[6];
        cards[0] = toCard(R.string.number_of_dives_title, String.valueOf(numberOfDives),
                R.drawable.ic_hash_small);

        cards[1] = toCard(R.string.average_speed,
                compositeUnitAverageSpeed.toString(), R.drawable.speed_blue_small);

        cards[2] = toCard(R.string.average_top_speed, compositeUnitAverageMaxSpeed.toString(),
                R.drawable.speed_red_small);

        cards[3] = toCard(R.string.average_exit_altitude,
                String.valueOf(averageExitAltitudeMetric) + metricPreference.signature,
                R.drawable.ic_ruler_small);

        cards[4] = toCard(R.string.average_deploy_altitude,
                String.valueOf(averageDeployAltitudeMetric) + metricPreference.signature,
                R.drawable.ic_deploy_altitude_small);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(latestJumpDate);

        cards[5] = toCard(R.string.latest_jump_date, cal.getTime().toString(),
                R.drawable.ic_calendar);

        return cards;

    }

    private Card toCard(int title, String description, int icon) {
        SmallImageCard card = new SmallImageCard(this);
        card.setTitle(title);
        card.setDescription(description);
        card.setDrawable(icon);

        return card;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logbook_summary, menu);
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
