/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AdsManager.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年9月19日
 */
package com.miui.videoplayer.ads;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.miui.ad.api.BaseCell;
import com.xiaomi.miui.ad.api.PubConstants;
import com.xiaomi.miui.ad.api.VideoAdCell;
import com.xiaomi.miui.ad.api.XiaomiAdManager;
import com.xiaomi.miui.ad.listeners.AdEventListener;

/**
 * @author tianli
 *
 */
public class AdsManager {
    
    private final static String TAG = "AdsManager";
    
    private final static String PUBLISHER_ID = "8c546ced291f39ca0d3b809a60914db9";
    private final static String PUBLISHER_TOKEN = "35617820";
    
    private final static String KEY_CLICK_URL  = "clickUrl";
    
    private static AdsManager sInstance  = null;
    
    private XiaomiAdManager mManager;
    private AdEventListener mListener;

    private Context mContext;
    
    public synchronized  static AdsManager getInstance(Context context){
    	if(sInstance == null){
    		sInstance = new AdsManager(context);
    	}
    	return sInstance;
    }
    
    private AdsManager(Context context){
    	mContext = context.getApplicationContext();
    	try{
    	       mManager = XiaomiAdManager.getInstance(mContext,
    	                mContext.getPackageName(), PUBLISHER_ID,  PUBLISHER_TOKEN);
    	        mListener = mManager.getAdEventListener();
    	        //100k
    	        mManager.setAverageNetworkBandWidth(100);
    	        //100M
    	        mManager.setUplimitCacheFolderSize(50);
    	        new NetworkBroadcastReceiver().register();
    	}catch(Exception e){
    	}
    }
    
    public void cacheAdsList(){
        try{
            JSONObject json = new JSONObject();
        	json.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
            mListener.onAdCacheDownload(null, json);
    	}catch(Exception e){
    	}
    }
    
    public void cacheAdsList(int mediaId, int ci,  int source){
        try{
            JSONObject json  = AdUtils.buildOnlineJson(mediaId, ci, source);
            json.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
            mListener.onAdCacheDownload("" + mediaId, json);
        }catch(Exception e){
        }
    }
    
    public AdBean  getAdUrl(int mediaId, int ci,  int source){
        try{
            JSONObject json  = AdUtils.buildOnlineJson(mediaId, ci, source);
            json.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
            BaseCell baseCell = mListener.onAdRequest("" + mediaId, json);
            if(baseCell instanceof VideoAdCell){
                VideoAdCell videoCell = (VideoAdCell)baseCell;
                return new AdBean(videoCell);
            }
        }catch(Exception e){
        }
        return null;
    }
    
    public AdBean getAdUrl(int tvId, int source, String tvProgram){
        try{
            JSONObject sessionJson = AdUtils.buildLiveJson(tvId, source, tvProgram);
            sessionJson.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
            mListener.onAdCacheDownload(tvId + "",  sessionJson);
            BaseCell baseCell = mListener.onAdRequest("" + tvId, sessionJson);
            if(baseCell instanceof VideoAdCell){
                VideoAdCell videoCell = (VideoAdCell)baseCell;
                return new AdBean(videoCell);
            }
        }catch(Exception e){
        }
        return null;
    }
    
//    public JSONObject buildSessionJson(int mediaId, int ci, int source){
//    	JSONObject json = new JSONObject();
//    	try{
//        	json.put("mediaId", mediaId + "");	
//        	json.put("source", source + "");
//            json.put("ci", ci + "");
//        	json.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
//    	}catch(Exception e){
//    	}
//    	return json;
//    }
//    
//    public JSONObject buildSessionJson(int tvId, int source, String tvProgram){
//        JSONObject json = new JSONObject();
//        try{
//            json.put("tvId", tvId + "");  
//            json.put("source", source + "");
//            json.put("tvProgram", tvProgram);
//            json.put(PubConstants.TV_INPUT_SUPPORTFORMAT, "mp4, m3u8, ts" );
//        }catch(Exception e){
//        }
//        return json;
//    }
    
    private JSONObject getJsonObject(String extraJson){
        try{
            if(!TextUtils.isEmpty(extraJson)){
                return new JSONObject(extraJson);
            }
        }catch(Exception e){
        }
        return new JSONObject();
    }
    
    public void logAdPlay(AdBean ad, String extraJson){
        if(ad == null || TextUtils.isEmpty(ad.mAdId)){
            return;
        }
        Log.d(TAG, "logAdPlay ");
        JSONObject logextra = getJsonObject(extraJson);
        try {
            if(!TextUtils.isEmpty(ad.getAdSession())){
                logextra.put(PubConstants.AD_INPUT_SESSION,  ad.getAdSession());
            }
            mManager.getAdEventListener().onAdView(ad.getAdId(), logextra);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void logAdClick(AdBean ad, String extraJson){
        if(ad == null || TextUtils.isEmpty(ad.mAdId)){
            return;
        }
        Log.d(TAG, "logAdClick ");
        try{
            JSONObject logextra = getJsonObject(extraJson);
            try {
                if(!TextUtils.isEmpty(ad.getAdSession())){
                    logextra.put(PubConstants.AD_INPUT_SESSION,  ad.getAdSession());
                }
                if(!TextUtils.isEmpty(ad.getClickUrl())){
                    logextra.put(KEY_CLICK_URL,  ad.getClickUrl());
                }
                mManager.getAdEventListener().onAdClicked(ad.getAdId(), logextra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void logAdSkipped(AdBean ad, int playTime, String extraJson){
        if(ad == null || TextUtils.isEmpty(ad.mAdId)){
            return;
        }
        Log.d(TAG, "logAdSkipped : " +  playTime);
        try{
            JSONObject logextra = getJsonObject(extraJson);
            if(!TextUtils.isEmpty(ad.getAdSession())){
            	logextra.put(PubConstants.AD_INPUT_SESSION,  ad.getAdSession());
            }
            logextra.put(PubConstants.TV_INPUT_SKIP_PLAYTIME,  playTime + "");
            mManager.getAdEventListener().onAdSkipped(ad.getAdId(), logextra);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void logAdFinished(AdBean ad, String extraJson){
        if(ad == null || TextUtils.isEmpty(ad.mAdId)){
            return;
        }
        Log.d(TAG, "logAdFinished.");
        try{
            JSONObject logextra = getJsonObject(extraJson);
            if(!TextUtils.isEmpty(ad.getAdSession())){
            	logextra.put(PubConstants.AD_INPUT_SESSION,  ad.getAdSession());
            }
            mManager.getAdEventListener().onAdFinished(ad.getAdId(), logextra);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null){
                return;
            }
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if(mManager != null){
                    mManager.onNetworkChange(context, intent);
                }
            }
        }

        public void register() {
            try{
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mContext.getApplicationContext().registerReceiver(this, intentFilter);
            }catch(Exception e){
            }
        }
    }
}
