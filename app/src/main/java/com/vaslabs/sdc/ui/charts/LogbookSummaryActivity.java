package com.vaslabs.sdc.ui.charts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.vaslabs.logbook.Logbook;
import com.vaslabs.logbook.LogbookAPI;
import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.sdc.ui.R;
import java.util.Calendar;
import java.util.List;

import com.vaslabs.units.*;
import com.vaslabs.units.composite.VelocityUnit;
public class LogbookSummaryActivity extends Activity {

    static MaterialListView logbookSummaryListView = null;
    static Card[] viewCards;
    static DistanceUnit lastSelectedDistanceUnit = DistanceUnit.KM;
    static TimeUnit lastSelectedTimeUnit = TimeUnit.HOURS;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_summary);
        CommunicationManager.getInstance(this);
        context = this;
        logbookSummaryListView = (MaterialListView) findViewById(R.id.logbookSummaryListView);
        logbookSummaryListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView view, final int position) {
                Card clickedCard = viewCards[position];
                if (!(clickedCard instanceof SmallImageUnitsCard))
                    return;

                SmallImageUnitsCard unitsCard = (SmallImageUnitsCard) clickedCard;
                if (!unitsCard.areCompositeUnitOptionsAvailable()) {
                    unitsCard.switchToNext();
                    return;
                }

                final DistanceUnit[] distanceUnits = DistanceUnit.values();
                String[] items = new String[distanceUnits.length];

                for (int i = 0; i < items.length; i++) {
                    items[i] = distanceUnits[i].name();
                }

                new MaterialDialog.Builder(view.getContext())
                        .title(R.string.pick_distance_metric)
                        .items(items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                lastSelectedDistanceUnit = distanceUnits[which];
                                showTimeUnitsDialog(position);
                                return true;
                            }
                        })
                        .show().setSelectedIndex(lastSelectedDistanceUnit.ordinal());

            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {
            }
        });
        new LogbookFetchTask().execute();

    }

    private void showTimeUnitsDialog(final int position) {
        final TimeUnit[] timeUnits = TimeUnit.values();
        String[] items = new String[timeUnits.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = timeUnits[i].name();
        }
        new MaterialDialog.Builder(this)
                .title(R.string.pick_distance_metric)
                .items(items)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        lastSelectedTimeUnit = timeUnits[which];
                        dialog.dismiss();
                        for (int i = 0; i < viewCards.length; i++) {
                            if (viewCards[i] instanceof SmallImageUnitsCard)
                                ((SmallImageUnitsCard) viewCards[i]).switchTo(lastSelectedDistanceUnit, lastSelectedTimeUnit);
                        }
                        return true;
                    }
                })
                .show().setSelectedIndex(lastSelectedTimeUnit.ordinal());
    }

    private Card[] toCards(LogbookSummary logbookSummary, DistanceUnit distancePref, TimeUnit timePref) {
        return toCards(logbookSummary, distancePref, null, timePref);
    }

    protected static Card[] toCards(LogbookSummary logbookSummary, DistanceUnit metricPreference, DistanceUnit speedPreference, TimeUnit timeUnitPreference) {
        if (speedPreference == null)
            speedPreference = metricPreference;
        float averageDeployAltitude = logbookSummary.getAverageDeployAltitude();
        double averageDeployAltitudeMetric = metricPreference.convert(DistanceUnit.METERS,
                averageDeployAltitude);

        float averageExitAltitude = logbookSummary.getAverageExitAltitude();
        double averageExitAltitudeMetric = metricPreference.convert(DistanceUnit.METERS,
                averageExitAltitude);

        double averageSpeed = logbookSummary.getAverageSpeed();

        VelocityUnit compositeUnitAverageSpeed =
                new VelocityUnit(DistanceUnit.METERS, TimeUnit.SECONDS, averageSpeed);

        compositeUnitAverageSpeed = compositeUnitAverageSpeed.convert(speedPreference,
                timeUnitPreference);


        double averageMaxSpeed = logbookSummary.getAverageTopSpeed();
        VelocityUnit compositeUnitAverageMaxSpeed =
                new VelocityUnit(DistanceUnit.METERS, TimeUnit.SECONDS, averageMaxSpeed);

        compositeUnitAverageMaxSpeed = compositeUnitAverageMaxSpeed.convert(speedPreference,
                timeUnitPreference);

        int numberOfDives = logbookSummary.getNumberOfJumps();
        long latestJumpDate = logbookSummary.getLatestJumpDate();


        Card[] cards = new Card[6];
        cards[0] = toCard(R.string.number_of_dives_title, String.valueOf(numberOfDives),
                R.drawable.ic_hash_small);

        cards[1] = toCard(R.string.average_speed,
                compositeUnitAverageSpeed, R.drawable.speed_blue_small);

        cards[2] = toCard(R.string.average_top_speed, compositeUnitAverageMaxSpeed,
                R.drawable.speed_red_small);

        cards[3] = toCard(R.string.average_exit_altitude,
                averageExitAltitudeMetric, metricPreference,
                R.drawable.ic_ruler_small);

        cards[4] = toCard(R.string.average_deploy_altitude,
                averageDeployAltitudeMetric, metricPreference,
                R.drawable.ic_deploy_altitude_small);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(latestJumpDate);

        cards[5] = toCard(R.string.latest_jump_date, cal.getTime().toString(),
                R.drawable.ic_calendar);

        return cards;

    }

    private static Card toCard(int title, double averageExitAltitudeMetric, DistanceUnit metricPreference, int ic_ruler_small) {
        SmallImageUnitsCard smallImageUnitsCard = new SmallImageUnitsCard(context, metricPreference, averageExitAltitudeMetric);
        smallImageUnitsCard.setTitle(title);
        smallImageUnitsCard.setDrawable(ic_ruler_small);
        return smallImageUnitsCard;
    }

    private static Card toCard(int title, String description, int icon) {
        SmallImageCard card = new SmallImageCard(context);
        card.setTitle(title);
        card.setDescription(description);
        card.setDrawable(icon);

        return card;
    }

    private static Card toCard(int title, VelocityUnit unit, int icon) {
        SmallImageUnitsCard smallImageUnitsCard = new SmallImageUnitsCard(context, unit);
        smallImageUnitsCard.setTitle(title);
        smallImageUnitsCard.setDrawable(icon);
        return smallImageUnitsCard;
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
        Intent intent = null;
        switch (id) {
            case R.id.barometer_chart:
                intent = new Intent(this, BarometerChartActivity.class);
            case R.id.velocity_chart:
                intent = new Intent(this, VelocityChartActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

class SmallImageUnitsCard extends SmallImageCard {

    private DistanceUnit distanceUnit;
    private VelocityUnit compositeUnit;

    private double value;

    public SmallImageUnitsCard(Context context, DistanceUnit du, double value) {
        this(context, du, null);
        this.setDescription(du.toString(value));
        this.value = value;
    }

    public SmallImageUnitsCard(Context context, VelocityUnit cu) {
        this(context, null, cu);
        this.setDescription(cu.toString());
    }

    private SmallImageUnitsCard(Context context, DistanceUnit du, VelocityUnit cu) {
        super(context);
        distanceUnit = du;
        compositeUnit = cu;
    }

    public void switchTo(DistanceUnit du, TimeUnit tu) {
        if (compositeUnit == null)
            return;

        compositeUnit = compositeUnit.convert(du, tu);
        this.setDescription(compositeUnit.toString());
    }

    private void switchTo(DistanceUnit du) {
        if (distanceUnit == null) {
            return;
        }
        value = du.convert(distanceUnit, value);
        this.setDescription(du.toString(value));
        this.distanceUnit = du;
    }

    public boolean areCompositeUnitOptionsAvailable() {
        return compositeUnit != null;

    }

    public void switchToNext() {
        if (distanceUnit == null)
            return;
        int next_ordinal = this.distanceUnit.ordinal() + 1;
        DistanceUnit[] units = DistanceUnit.values();
        if (next_ordinal >= units.length)
            next_ordinal = 0;
        DistanceUnit newDistanceUnit = units[next_ordinal];
        switchTo(newDistanceUnit);
    }

}

class LogbookFetchTask extends AsyncTask<Void, Void, List<Logbook>> {

    private Exception exception = null;

    @Override
    protected List<Logbook> doInBackground(Void... params) {
        List<Logbook> logbookEntries = null;
        LogbookSummary logbookSummary = null;
        try {
            logbookEntries = LogbookAPI.INSTANCE.getLogbookEntries();
            return logbookEntries;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Logbook> logbookEntries) {
        LogbookSummary logbookSummary = LogbookSummary.fromLogbookEntries(logbookEntries);

        LogbookSummaryActivity.viewCards = LogbookSummaryActivity.toCards(logbookSummary, DistanceUnit.FEET,
                LogbookSummaryActivity.lastSelectedDistanceUnit, LogbookSummaryActivity.lastSelectedTimeUnit);
        for (Card card : LogbookSummaryActivity.viewCards) {
            LogbookSummaryActivity.logbookSummaryListView.add(card);
        }


    }
}