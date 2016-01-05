package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Intent;

import com.vaslabs.sdc.ui.charts.StatsActivity;
import com.vaslabs.sdc.ui.fragments.actions.ActionManager;

/**
 * Created by vnicolaou on 05/01/16.
 */
public class StatsActionManager implements ActionManager {
    @Override
    public void manageAction(Activity activity) {
        Intent intent = new Intent(activity, StatsActivity.class);
        activity.startActivity(intent);
    }
}
