package com.vaslabs.sdc.ui;

import com.vaslabs.sdc.ui.fragments.AccountFragment;
import com.vaslabs.sdc.ui.fragments.actions.DefaultActionManager;

/**
 * Created by vnicolaou on 17/01/16.
 */
public class AccountActionManager extends DefaultActionManager {
    public AccountActionManager() {
        super(AccountFragment.class);
    }
}
