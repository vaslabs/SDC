package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.vaslabs.sdc.ui.fragments.ManageLogsFragment;

/**
 * Created by vnicolaou on 20/12/15.
 */
public class LogsSubmissionActionManager implements ActionManager {
    @Override
    public void manageAction(Activity activity) {
        Fragment fragment = new ManageLogsFragment();
        Bundle args = new Bundle();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, fragment)
                .commit();
    }
}
