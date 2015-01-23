package com.vaslabs.sdc.connectivity;

import android.content.Context;
import android.content.Intent;

public abstract class ConnectivityActionResponder {

    public abstract void manageAction( Context context, Intent intent );

}
