package com.vaslabs.sdc.ui;

import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.sdc.utils.SkyDiver;
import com.vaslabs.sdc.utils.SkyDiverEnvironmentUpdate;

public interface EnvironmentUpdate extends SkyDiverEnvironmentUpdate {
    
    void onBarometerValueChange(HPASensorValue hpaValue);
    void onGPSUpdate(Position newKnownPosition);
    
}
