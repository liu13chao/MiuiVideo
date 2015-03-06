/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AdsService.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年10月11日
 */
package com.miui.videoplayer.ads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.miui.videoplayer.ads.AdBean;
import com.miui.videoplayer.ads.AdsManager;

/**
 * @author tianli
 *
 */
public class AdsService extends Service {

    public final static String TAG = "AdsService";
    
    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
    
    public IAdsService.Stub mBinder = new IAdsService.Stub() {
        @Override
        public String getOnlineAd(int mediaId, int ci, int source)
                throws RemoteException {
            Log.d(TAG, "getOnlineAd : " + mediaId);
            AdBean  ad = AdsManager.getInstance(getApplicationContext()).getAdUrl(mediaId, ci, source);
            if(ad != null){
                return ad.toJson();
            }
            return null;
        }

        @Override
        public String getTvAd(int tvId, int source, String tvProgram)
                throws RemoteException {
            Log.d(TAG, "getTvAd : " + tvId);
            AdBean  ad = AdsManager.getInstance(getApplicationContext()).getAdUrl(tvId, source, tvProgram);
            if(ad != null){
                return ad.toJson();
            }
            return null;
        }

        @Override
        public void logAdPlay(String adBean, String extraJson)
                throws RemoteException {
            Log.d(TAG, "logAdPlay : " + adBean);
            AdsManager.getInstance(getApplicationContext()).logAdPlay(new AdBean(adBean), extraJson);
        }

        @Override
        public void logAdClick(String adBean, String extraJson)
                throws RemoteException {
            Log.d(TAG, "logAdClick : " + adBean);
            AdsManager.getInstance(getApplicationContext()).logAdClick(new AdBean(adBean), extraJson);
        }

        @Override
        public void logAdSkipped(String adBean,  int playTime, String extraJson)
                throws RemoteException {
            Log.d(TAG, "logAdSkipped : " + adBean);
            AdsManager.getInstance(getApplicationContext()).logAdSkipped(new AdBean(adBean), playTime, extraJson);
        }

        @Override
        public void logAdFinished(String adBean, String extraJson)
                throws RemoteException {
            Log.d(TAG, "logAdFinished : " + adBean);
            AdsManager.getInstance(getApplicationContext()).logAdFinished(new AdBean(adBean), extraJson);
        }
    };

}
