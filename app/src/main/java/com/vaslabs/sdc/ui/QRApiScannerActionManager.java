package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by vnicolaou on 20/12/15.
 */
public class QRApiScannerActionManager implements ActionManager {
    @Override
    public void manageAction(Activity activity) {
        Intent intent = new Intent(activity, QRApiScannerActivity.class);
        activity.startActivity(intent);
    }
}
