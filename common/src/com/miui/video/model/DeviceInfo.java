/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DeviceInfo.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-20
 */
package com.miui.video.model;

import miui.os.Build;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.miui.video.api.ApiConfig;
import com.miui.video.api.def.DeviceTypeValueDef;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 * @author tianli
 * 
 */
@SuppressLint("DefaultLocale")
public class DeviceInfo extends AppSingleton {
    public static final String TAG = DeviceInfo.class.getName();

    public final static int NETWORK_NONE = 0;
    public final static int NETWORK_WIFI = 1;
    public final static int NETWORK_MOBILE = 2;

    public final static String NETWORK_TYPE_WIFI = "wifi";
    public final static String NETWORK_TYPE_NONE = "unknown";

    private int mNetworkState = NETWORK_NONE;

    private int mDeviceType;
    private int mPlatform;
    private int mAppVersion;
    private String mDeviceId;
    private String mImei;
    private String mMacAddress;
    private String mAndroidId;
    private int mIpAddress;

    private String mApnType;
    private String mProxyHost;
    private int mProxyPort;

    private boolean mIsHM2 = false;

    @Override
    public void init(Context context) {
        super.init(context);
        initAppVersion(context);
        initPlatform();
        initNetworkInfo(context);
        refreshNetworkState(context);
        initDeviceId(context);
    }

    public String getHashedAndroidId() {
        if( mAndroidId != null && mAndroidId.length() > 0)
            return Util.getMD5(mAndroidId);
        return "0";
    }


    public int getAppVersion() {
        return mAppVersion;
    }

    public boolean isHM2(){
        return mIsHM2;
    }

    public static boolean isH2() {
        try{
            if (Build.IS_HONGMI_TWO && !Build.IS_HONGMI_TWO_A && !Build.IS_HONGMI_TWO_S) {
                return true;
            }
        }catch(Throwable e){
        }
        return "HM2013022".equals(Build.DEVICE) || "HM2013023".equals(Build.DEVICE);
    }

    public String getUID() {
        String md5Hash = Util.getMD5(mDeviceId);
        return md5Hash;
    }

    public String getImeiMd5() {
        if (mImei != null && mImei.length() > 0) {
            return Util.getMD5(mImei);
        }
        return "0";
    }

    //	public String getIMEI() {
    //		if (mImei != null && mImei.length() > 0) {
    //			return mImei;
    //		}
    //		return "0";
    //	}

    public String getMacAddress() {
        if (mMacAddress != null && mMacAddress.length() > 0) {
            return mMacAddress;
        }
        return "0";
    }

    public int getIpAddress() {
        return mIpAddress;
    }

    public boolean hasConnectivity() {
        refreshNetworkState(mContext);
        return mNetworkState != NETWORK_NONE;
    }

    public boolean isWifiUsed() {
        refreshNetworkState(mContext);
        return mNetworkState == NETWORK_WIFI;
    }

    public boolean isWapApnUsed() {
        refreshNetworkState(mContext);
        if (mNetworkState == NETWORK_MOBILE) {
            if (mApnType != null && mApnType.contains("wap")) {
                if (mProxyHost != null && mProxyHost.length() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getNetworkState() {
        refreshNetworkState(mContext);
        return mNetworkState;
    }

    public String getNetworkType() {
        refreshNetworkState(mContext);
        if (mNetworkState == NETWORK_WIFI) {
            return NETWORK_TYPE_WIFI;
        } else if (mApnType != null && mApnType.length() > 0) {
            return mApnType;
        }
        return NETWORK_TYPE_NONE;
    }

    public int getDeviceType() {
        return mDeviceType;
    }

    public int getPlatform() {
        return mPlatform;
    }

    public String getProxyHost() {
        return mProxyHost;
    }

    public int getProxyPort() {
        return mProxyPort;
    }

    public String getModelInfo() {
        return Build.MODEL;
    }

    public String getUserAgent() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Build.MODEL);
        strBuilder.append(" ");
        String fingerPrint = Build.FINGERPRINT;
        strBuilder.append(fingerPrint);
        return strBuilder.toString();
    }

    public static boolean isSupportPriorityStorage() {
        //    	if (Build.IS_HONGMI_DUAL_CARD) {
        //    		return true;
        //    	}
        return false;
    }

    private void initPlatform() {
        String brand = Build.BRAND.toLowerCase();
        if (brand.contains("xiaomi")) {
            String device = Build.DEVICE;
            if (device.equals("mione") || device.equals("mione_plus")) {
                mPlatform =  ApiConfig.PLATFORM_MI_ONE;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PHONE;
                return;
            } else if (device.equals("aries") || device.equals("taurus")) {
                mPlatform =  ApiConfig.PLATFORM_MI_TWO;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PHONE;
                return;
            } else if (device.equals("pisces") || device.equals("cancro")) {
                mPlatform =  ApiConfig.PLATFORM_MI_THREE;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PHONE;
                return;
            } else if (mIsHM2 || device.equals("wt93007") || device.equals("HM2013023")) {
                mPlatform =  ApiConfig.PLATFORM_MI_RED_TWO;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PHONE;
                return;
            } else if(device.equals("mocha")) {
                mPlatform =  ApiConfig.PLATFORM_PAD_N7;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PAD;
                return;
            }
        }
        if(brand.contains("google")) {
            String device = Build.DEVICE;
            if(device.equals("flo")) {
                mPlatform =  ApiConfig.PLATFORM_PAD_N7;
                mDeviceType = DeviceTypeValueDef.DEVICE_TYPE_PAD;
                return;
            }
        }

        int wsize = Util.getScreenWidth(mContext);
        if (wsize >= 1080) {
            mPlatform =  ApiConfig.PLATFORM_ANDROID_SUPER_RESOLUTION;
            return;
        } else if (wsize >= 720) {
            mPlatform =  ApiConfig.PLATFORM_ANDROID_HIGH_RESOLUTION;
            return;
        } else {
            mPlatform =  ApiConfig.PLATFORM_ANDROID_LOW_RESOLUTION;
            return;
        }
    }

    private void initDeviceId(Context context) {
        mAndroidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        DKLog.d(TAG, "androidId : " + mAndroidId);
        mDeviceId = DeviceIDCache.getDeviceID();
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            try {
                mImei = tm.getDeviceId(); // get imei
            } catch (Exception e) {
                DKLog.d(TAG, e.getLocalizedMessage());
            }
            //			DKLog.d(TAG, "imei : " + mImei);
            if (Util.isEmpty(mDeviceId)) {
                mDeviceId = mImei;
            }
        } 

        if (Util.isEmpty(mDeviceId)) {
            mDeviceId = mMacAddress;
        }

        if (Util.isEmpty(mDeviceId)) {
            mDeviceId = mAndroidId;
        }
        // differentiate between phone and pad.
        DeviceIDCache.setDeviceID(mDeviceId);
    }

    private void initAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if(packageInfo != null) {
                mAppVersion = packageInfo.versionCode;
            }
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage());
        }
    }

    private void initNetworkInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            mMacAddress = wifiInfo.getMacAddress();
            mIpAddress = wifiInfo.getIpAddress();
        }
    }

    private void refreshNetworkState(Context context) {
        mNetworkState = NETWORK_NONE;
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            int nType = activeNetworkInfo.getType();
            if (nType == ConnectivityManager.TYPE_WIFI) {
                mNetworkState = NETWORK_WIFI;
            } else if (nType == ConnectivityManager.TYPE_MOBILE) {
                mNetworkState = NETWORK_MOBILE;
                Uri apnUri = Uri
                        .parse("content://telephony/carriers/preferapn");
                try {
                    Cursor cursor = context.getContentResolver().query(apnUri,
                            new String[] { "name", "apn", "proxy", "port" },
                            "current=1", null, null);
                    if (cursor != null) {
                        int count = cursor.getCount();
                        cursor.moveToFirst();
                        for (int i = 0; i < count; i++) {
                            mApnType = cursor.getString(1);
                            mProxyHost = cursor.getString(2);
                            String port = cursor.getString(3);
                            mProxyPort = 80;
                            try {
                                mProxyPort = Integer.parseInt(port);
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (Exception e) {
                    DKLog.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }
}
