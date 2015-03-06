package com.miui.videoplayer.statistic;

import java.util.UUID;

import android.content.Context;

import com.miui.video.statistic.UploadStatisticInfoManager;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineUri;

public class StartPlayStatistics {
	
    private Context mContext;
    private BaseUri mUri;
    private UUID mUuid;
    private TelevisionInfo mTvInfo;
	public StartPlayStatistics(Context context, BaseUri uri, UUID uuid){
        mContext = context;
        mUri = uri;
        mUuid = uuid;
	}
	
	public StartPlayStatistics(TelevisionInfo tvinfo, UUID uuid){
		mTvInfo = tvinfo;
		mUuid = uuid;
	}
	
    public void onPrepared(boolean isAds){
       if(mUri != null && mUri instanceof OnlineUri){
    	   OnlineUri onlineuri = (OnlineUri)mUri;
    	   int mediaid = onlineuri.getMediaId();
    	   int ci = onlineuri.getCi();
    	   int source = onlineuri.getSource();
    	   int clarity = onlineuri.getResolution();
    	   int videoType = onlineuri.getResolution();
    	   int playType = onlineuri.getPlayType();
    	   long timestamp = System.currentTimeMillis();
    	   boolean isads = isAds;
    	   UploadStatisticInfoManager.uploadStartPlayStatistic(mediaid, ci, source, clarity, videoType, playType, timestamp, isads, mUuid);   
       }else if(mTvInfo != null){
    	   long timestamp = System.currentTimeMillis();
    	   int source = mTvInfo.source;
    	   int epgid = mTvInfo.epgid;
    	   String cmccid = mTvInfo.cmccid;
    	   String tvid;
    	   if(source == 0){
    		   tvid = String.valueOf(epgid);
    	   }else{
    		   tvid = cmccid;
    	   }
    	   String videoidentifying = mTvInfo.videoidentifying;
    	   int channelid = mTvInfo.channelid;
    	   String channelname = mTvInfo.channelname;
    	   boolean isads = isAds;
    	   UploadStatisticInfoManager.uploadStartLiveStatistic(channelid, channelname, tvid, source, videoidentifying, timestamp, isads, mUuid);
       }
    }
}
