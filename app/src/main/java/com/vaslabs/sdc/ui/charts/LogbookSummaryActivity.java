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

import java.util.Calendar;

public class LogbookSummaryActivity extends Activity {

    MaterialListView logbookSummaryListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logbook_summary);

        logbookSummaryListView = (MaterialListView) findViewById(R.id.logbookSummaryListView);
        LogbookSummary logbookSummary = LogbookAPI.MOCK.getLogbookSummary(null, this);

        Card[] viewCards = toCards(logbookSummary);

        for (Card card : viewCards) {
            logbookSummaryListView.add(card);
        }

    }

    private Card[] toCards(LogbookSummary logbookSummary) {
        int averageDeployAltitude = logbookSummary.getAverageDeployAltitude();
        int averageExitAltitude = logbookSummary.getAverageExitAltitude();
        double averageSpeed = logbookSummary.getAverageSpeed();
        double averageMaxSpeed = logbookSummary.getAverageTopSpeed();
        int numberOfDives = logbookSummary.getNumberOfJumps();
        long latestJumpDate = logbookSummary.getLatestJumpDate();


        Card[] cards = new Card[6];
        cards[0] = toCard(R.string.number_of_dives_title, String.valueOf(numberOfDives),
                R.drawable.ic_hash_small);

        cards[1] = toCard(R.string.average_speed,
                String.valueOf(averageSpeed), R.drawable.speed_blue_small);

        cards[2] = toCard(R.string.average_top_speed, String.valueOf(averageMaxSpeed),
                R.drawable.speed_red_small);

        cards[3] = toCard(R.string.average_exit_altitude, String.valueOf(averageExitAltitude),
                R.drawable.ic_ruler_small);

        cards[4] = toCard(R.string.average_deploy_altitude, String.valueOf(averageDeployAltitude),
                R.drawable.ic_deploy_altitude_small);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(latestJumpDate);

        cards[5] = toCard(R.string.latest_jump_date, cal.getTime().toString(), R.drawable.ic_calendar);

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
