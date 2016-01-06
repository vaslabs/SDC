package com.vaslabs.sdc.ui.charts;

import android.app.Activity;
import android.content.Intent;

import com.vaslabs.sdc.ui.MapMySessionActivity;
import com.vaslabs.sdc.ui.fragments.actions.ActionManager;

/**
 * Created by vnicolaou on 06/01/16.
 */
public class MapActionManager implements ActionManager {
    @Override
    public void manageAction(Activity activity) {
        Intent intent = new Intent(activity, MapMySessionActivity.class);
        activity.startActivity(intent);
    }
}
