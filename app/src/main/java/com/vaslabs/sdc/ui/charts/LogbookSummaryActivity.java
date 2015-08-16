package com.vaslabs.sdc.ui.charts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
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
    Card[] viewCards;
    private DistanceUnit lastSelectedDistanceUnit = DistanceUnit.KM;
    private TimeUnit lastSelectedTimeUnit = TimeUnit.HOURS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_summary);

        logbookSummaryListView = (MaterialListView) findViewById(R.id.logbookSummaryListView);
        LogbookSummary logbookSummary = LogbookAPI.MOCK.getLogbookSummary(null, this);

        viewCards = toCards(logbookSummary, DistanceUnit.FEET, lastSelectedDistanceUnit, lastSelectedTimeUnit);

        for (Card card : viewCards) {
            logbookSummaryListView.add(card);
        }

        logbookSummaryListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView view, final int position) {
                Card clickedCard = viewCards[position];
                if (!(clickedCard instanceof SmallImageUnitsCard))
                    return;

                SmallImageUnitsCard unitsCard = (SmallImageUnitsCard)clickedCard;
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

        CompositeUnit compositeUnitAverageSpeed =
                new CompositeUnit(DistanceUnit.METERS, TimeUnit.SECONDS, averageSpeed);

        compositeUnitAverageSpeed = compositeUnitAverageSpeed.convert(speedPreference,
                timeUnitPreference);


        double averageMaxSpeed = logbookSummary.getAverageTopSpeed();
        CompositeUnit compositeUnitAverageMaxSpeed =
                new CompositeUnit(DistanceUnit.METERS, TimeUnit.SECONDS, averageMaxSpeed);

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

    private Card toCard(int title, double averageExitAltitudeMetric, DistanceUnit metricPreference, int ic_ruler_small) {
        SmallImageUnitsCard smallImageUnitsCard = new SmallImageUnitsCard(this, metricPreference, averageExitAltitudeMetric);
        smallImageUnitsCard.setTitle(title);
        smallImageUnitsCard.setDrawable(ic_ruler_small);
        return smallImageUnitsCard;
    }

    private Card toCard(int title, String description, int icon) {
        SmallImageCard card = new SmallImageCard(this);
        card.setTitle(title);
        card.setDescription(description);
        card.setDrawable(icon);

        return card;
    }

    private Card toCard(int title, CompositeUnit unit, int icon) {
        SmallImageUnitsCard smallImageUnitsCard = new SmallImageUnitsCard(this, unit);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

class SmallImageUnitsCard extends SmallImageCard {

    private DistanceUnit distanceUnit;
    private CompositeUnit compositeUnit;

    private double value;

    public SmallImageUnitsCard(Context context, DistanceUnit du, double value) {
        this(context, du, null);
        this.setDescription(du.toString(value));
        this.value = value;
    }

    public SmallImageUnitsCard(Context context, CompositeUnit cu) {
        this(context, null, cu);
        this.setDescription(cu.toString());
    }

    private SmallImageUnitsCard(Context context, DistanceUnit du, CompositeUnit cu) {
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