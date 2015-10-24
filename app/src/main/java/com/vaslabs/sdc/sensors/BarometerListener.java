package com.vaslabs.sdc.sensors;

public interface BarometerListener {
    void onHPASensorValueChange(HPASensorValue value, MetersSensorValue altitude, MetersSensorValue deltaAltitude);
}
