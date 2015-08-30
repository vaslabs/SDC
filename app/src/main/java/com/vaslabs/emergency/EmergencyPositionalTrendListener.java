package com.vaslabs.emergency;

import com.vaslabs.sdc.types.TrendPoint;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.sdc.utils.TrendDirection;
import com.vaslabs.sdc.utils.TrendListener;
import com.vaslabs.sdc.utils.VelocityState;
import com.vaslabs.units.TimeUnit;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class EmergencyPositionalTrendListener implements TrendListener {

    int timesCalled = 0;
    int noOfSentSms = 0;
    final double timeLapse;
    double currentTimeLapse;
    final TimeUnit timeUnit = TimeUnit.MINUTES;
    final long firstTimeCalledTimestamp;

    public EmergencyPositionalTrendListener(double timeLapse, TimeUnit timeUnit) {
        this.timeLapse = timeUnit.convert(timeLapse, timeUnit);
        this.currentTimeLapse = timeLapse;
        firstTimeCalledTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onTrendEvent() {
        int diff = (int)(System.currentTimeMillis() - firstTimeCalledTimestamp);
        double value = timeUnit.convert(diff, TimeUnit.MILLISECONDS);
        if (value >= currentTimeLapse) {
            sendEmergencySms();
            currentTimeLapse += timeLapse;
        }
        timesCalled++;

    }

    private void sendEmergencySms() {
        noOfSentSms += 1;
    }

    @Override
    public VelocityState getVelocityState() {
        return VelocityState.CONSTANT;
    }

    @Override
    public TrendPoint getValueLimit() {
        return null; //should never be called because getDirectionalAction is NEUTRAL
    }

    @Override
    public TrendDirection getDirectionAction() {
        return TrendDirection.NEUTRAL;
    }
}
