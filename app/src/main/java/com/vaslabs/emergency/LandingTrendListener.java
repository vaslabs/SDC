package com.vaslabs.emergency;

import android.content.Context;

import com.vaslabs.logbook.SaveDataTrendListener;
import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.units.TimeUnit;
import com.vaslabs.vtrends.StrategyVisitor;
import com.vaslabs.vtrends.impl.AbstractTrendStrategy;

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
            SkyDivingEnvironment.getInstance().logLanding();
            EmergencyPreferences ep = EmergencyPreferences.load(mContext);
            double minutes = ep.getMinimumTimeBeforeCall(TimeUnit.MINUTES);
            trendStrategy.registerEventListener(new EmergencyPositionalTrendListener(minutes, TimeUnit.MINUTES, mContext));
            trendStrategy.registerEventListener(new SaveDataTrendListener());
            wasCalled = true;
        }
    }
}
