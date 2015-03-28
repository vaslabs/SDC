package com.vaslabs.sdc.utils;

public interface SkyDiverEnvironmentUpdate {
    void onNewSkydiverInfo(SkyDiver skydiver);
    void onSkydiverInfoUpdate(SkyDiver skydiver);
    void onConnectivityChange(SkyDiver skydiver);
    void onLooseConnection(SkyDiver skydiver);
    void onLooseConnection(String skydiverKey);
}
