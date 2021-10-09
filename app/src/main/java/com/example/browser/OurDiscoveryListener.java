package com.example.browser;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class OurDiscoveryListener implements NsdManager.DiscoveryListener {
    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {

    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {

    }

    @Override
    public void onDiscoveryStarted(String serviceType) {

    }

    @Override
    public void onDiscoveryStopped(String serviceType) {

    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {

    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {

    }
}
