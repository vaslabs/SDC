package com.vaslabs.sdc.connectivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public abstract class ConnectivityActionResponder<V extends View> {
    
    private boolean knownState = false;
    
    public abstract void manageAction( Context context, Intent intent );

    public abstract V getView();
    
    public boolean stateIsKnown() {
        return knownState;
    }
    
    public void setStateKnown() {
        knownState = true;
    }
    
    public void setStateUnknown() {
        knownState = false;
    }
}
