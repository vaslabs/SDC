package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import com.vaslabs.sdc.ui.fragments.CardViewFragment;

/**
 * Created by vnicolaou on 20/12/15.
 */
public class LogbookSummaryActionManager implements ActionManager {
    @Override
    public void manageAction(Activity activity) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new CardViewFragment();
        Bundle args = new Bundle();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, fragment)
                .commit();

    }

}
