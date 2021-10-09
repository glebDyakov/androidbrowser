package com.example.browser;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class OurResolveListener implements NsdManager.ResolveListener {
    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

    }

    @Override
    public void onServiceResolved(NsdServiceInfo serviceInfo) {

    }
}
