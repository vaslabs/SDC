package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.vaslabs.sdc.ui.fragments.ManageLogsFragment;

/**
 * Created by vnicolaou on 20/12/15.
 */
public class LogsSubmissionActionManager extends DefaultActionManager{
    public LogsSubmissionActionManager() {
        super(ManageLogsFragment.class);
    }
}
