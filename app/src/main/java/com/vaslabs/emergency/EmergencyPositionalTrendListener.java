package com.vaslabs.emergency;

import android.content.Context;
import android.telephony.SmsManager;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
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
    final Context mContext;
    public EmergencyPositionalTrendListener(double timeLapse, TimeUnit timeUnit, Context mContext) {
        this.timeLapse = timeUnit.convert(timeLapse, timeUnit);
        this.currentTimeLapse = timeLapse;
        firstTimeCalledTimestamp = System.currentTimeMillis();
        this.mContext = mContext;
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
        SmsManager smsManager = SmsManager.getDefault();
        EmergencyPreferences ep = EmergencyPreferences.load(mContext);
        for (EmergencyContact ec : ep.getEmergencyContactList())
            smsManager.sendTextMessage(ec.phoneNumber, null, SkyDivingEnvironment.getInstance().getLastPositionMessage(), null, null);
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
