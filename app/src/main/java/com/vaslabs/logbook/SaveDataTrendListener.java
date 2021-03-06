package com.vaslabs.logbook;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.vtrends.TrendListener;
import com.vaslabs.vtrends.types.TrendDirection;
import com.vaslabs.vtrends.types.TrendPoint;
import com.vaslabs.vtrends.types.VelocityState;

/**
 * Created by vnicolaou on 31/10/15.
 */
public class SaveDataTrendListener implements TrendListener {
    boolean hasBeenCalled = false;
    @Override
    public void onTrendEvent() {
        if (!hasBeenCalled) {
            hasBeenCalled = true;
            try {
                SkyDivingEnvironment.getInstance().writeSensorLogs();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public VelocityState getVelocityState() {
        return VelocityState.CONSTANT;
    }

    @Override
    public TrendPoint getValueLimit() {
        return null;
    }

    @Override
    public TrendDirection getDirectionAction() {
        return TrendDirection.NEUTRAL;
    }
}
