package com.miui.videoplayer.framework.milink;

import com.miui.videoplayer.framework.airkan.RemoteTVMediaPlayerControl;

import java.util.HashMap;

import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.views.OriginMediaController;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

public class AirPlayHelper {
    private static final String TAG = "AirPlayHelper";

    private OriginMediaController mMediaController;
    private DuoKanMediaController mDuokanMediaController;

    private LocalMediaPlayerControl mLocalPhoneMediaControl;
    private RemoteTVMediaPlayerControl mRemoteTVMediaControl;

    private Context mContext;

    private NsdManager mNsdManager;
    private HashMap<String, NsdServiceInfo> mDevices;

    private NsdManager.DiscoveryListener mNsdDiscoveryListener = new NsdManager.DiscoveryListener() {
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
