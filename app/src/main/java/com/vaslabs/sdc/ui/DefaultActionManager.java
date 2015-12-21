package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;

import com.vaslabs.sdc.ui.fragments.ManageLogsFragment;

/**
 * Created by vnicolaou on 21/12/15.
 */
public abstract class DefaultActionManager<T extends Fragment> implements ActionManager{

    private final Class<T> fragmentClass;

    public DefaultActionManager(Class<T> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }



    @Override
    public void manageAction(Activity activity) {
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e("DEFAULT_ACTION_MANAGER", e.toString());
            return;
        }

        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, fragment)
                .commit();
    }
}
