package com.xiaomi.miui.pushads.sdk;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.xiaomi.miui.pushads.sdk.NotifyAdsManager.NetState;


/**
 * 网络相关的工具类， 得到网络状态，判断是否可以继续下载等等
 * @author liuwei
 *
 */
public class NetUtils  {

    public static boolean isEmptyString(String str) {
        if (TextUtils.isEmpty(str) || str.equals("0")) return true;
        return false;
    }

    public static NetState getNetState(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        //这个地方一定不能连续使用两个 connManager.getActiveNetworkInfo()
        if (connManager == null) {
            return NetState.NONE;
        }

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return NetState.NONE;
        }

        //非收费网络，表明是wifi状态
        if(!connManager.isActiveNetworkMetered()) {
            return NetState.Wifi;
        }

        final TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephony.getNetworkType();
        return getNetworkClass(networkType);
    }

    private static NetState getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetState.MN2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetState.MN3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetState.MN4G;
            default:
                return NetState.NONE;
        }
    }

    public static boolean canDownloadAds(Context context) {
        NetState curState = getNetState(context);
        boolean ret = true;

        //现在的规则是如果没有网络的情况下，会中断下载
        if (NetState.NONE == curState) {
            ret = false;
        }

        if (curState != NetState.Wifi) {
        }
        return ret;
    }

    /**
     * 获得本地的ip 地址
     * @return
     */
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                    .getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
                NetworkInterface intf = mEnumeration.nextElement();
                for (Enumeration<InetAddress> enumIPAddr = intf
                        .getInetAddresses(); enumIPAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    // 如果不是回环地址
                    if (!inetAddress.isLoopbackAddress()) {
                        // 直接返回本地IP地址
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Error", ex.toString());
        } catch (Exception e) {
            Log.e(NotifyAdsManager.TAG, "get ip address failed");
        }
        return null;
    }

    /**
     * 获得本机的imei 号
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return "";
        String imei = tm.getDeviceId();
        if (isEmptyString(imei))
            return "";

        return imei;
    }

    /**
     * 获得当前的userid
     */
    public static final String getXiaomiUserId(Context context) {
        Account[] accounts = null;
        try {
            accounts = AccountManager.get(context).getAccounts();
        } catch(Exception e) {
            Log.e(NotifyAdsManager.TAG, "get user account failed");
            return null;
        }

        if (accounts == null) {
            return null;
        }

        for (int i = 0; i < accounts.length; ++i) {
            if (accounts[i].type.equals("com.xiaomi")) {
                Account ret =  accounts[i];

                if (isEmptyString(ret.name))
                    return "";

                return ret.name;
            }
        }
        return null;
    }
}
