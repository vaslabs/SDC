package com.vaslabs.emergency;

import android.content.Context;

import com.vaslabs.sdc.utils.AbstractTrendStrategy;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.units.TimeUnit;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class LandingTrendListener extends DefaultBarometerTrendListener implements StrategyVisitor {
    private boolean wasCalled = false;
    private final Context mContext;
    public LandingTrendListener(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public void onTrendEvent() {

    }

    @Override
    public void visit(AbstractTrendStrategy trendStrategy) {
        if (!wasCalled) {
            EmergencyPreferences ep = EmergencyPreferences.load(mContext);
            double minutes = ep.getMinimumTimeBeforeCall(TimeUnit.MINUTES);
            trendStrategy.registerEventListener(new EmergencyPositionalTrendListener(minutes, TimeUnit.MINUTES, mContext));
            wasCalled = true;
        }
    }
}
