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
public class LogbookSummaryActionManager extends DefaultActionManager {
    public LogbookSummaryActionManager() {
        super(CardViewFragment.class);
    }
}
