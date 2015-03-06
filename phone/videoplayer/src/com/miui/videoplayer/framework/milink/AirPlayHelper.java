package com.miui.videoplayer.framework.milink;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class AirPlayHelper {
	
    public static final String TAG = "AirPlayHelper";

    public Context mContext;

    public NsdManager mNsdManager;
    public HashMap<String, NsdServiceInfo> mDevices;

    @SuppressLint("NewApi")
    public NsdManager.DiscoveryListener mNsdDiscoveryListener = new NsdManager.DiscoveryListener() {
        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        }

        @Override
        public void onServiceLost(NsdServiceInfo serviceInfo) {
        }

        @Override
        public void onServiceFound(NsdServiceInfo serviceInfo) {
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
        }

        @Override
        public void onDiscoveryStarted(String serviceType) {
        }
    };
}
