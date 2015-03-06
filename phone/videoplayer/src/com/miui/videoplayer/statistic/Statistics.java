/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Statistics.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-15
 */

package com.miui.videoplayer.statistic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.miui.videoplayer.common.DKTimeFormatter;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.widget.AdView.NotifyAdsPlayListener;
import com.miui.videoplayer.widget.MediaController.OnPauseOrStartListener;

/**
 * @author tianli
 *
 */
public class Statistics implements NotifyAdsPlayListener, OnPauseOrStartListener{

    public static final String STATISTIC_DURATION = "duration";
    public static final String STATISTIC_BUFFERTIME = "bufferTime";
    public static final String STATISTIC_DATE = "date";
    public static final String STATISTIC_PLAYINGTIME = "playingTime";
    public static final String STATISTIC_ADDURATION = "adduration";
    public static final String STATISTIC_ADPLAYDURATION = "adplayduration";
    public static final String STATISTIC_CONTENT = "content";
    public static final String STATISTIC_DATEPLAYINFO = "dateplayinfo";
    public static final String STATISTIC_MEDIAID = "mediaid";
    public static final String STATISTIC_SOURCE = "source";
    public static final String STATISTIC_RESOLUTION = "resolution";
    public static final String STATISTIC_MEDIACI = "mediaci";
    public static final String STATISTIC_MEDIAURL = "mediaurl";
    public static final String STATISTIC_PLAYINFOS = "playinfos";
    public static final String STATISTIC_OFFLINE = "offline";
    public static final String STATISTIC_All = "statistic";

    public static final String STATISTIC_INFO = "statisticinfo";

    public static final String STATISTIC_SUB_STARTTIME = "starttime";
    public static final String STATISTIC_SUB_ENDTIME = "endtime";
    public static final String STATISTIC_SUB_BACKGROUNDTIME = "backgroundtime";
    public static final String STATISTIC_SUB_BUFFERTIMES = "buffertimes";
    public static final String STATISTIC_SUB_BUFFERDURATION = "bufferduration";
    public static final String STATISTIC_SUB_ADTIME = "adtime";
    public static final String STATISTIC_SUB_LOADINGTIMES = "loadingtimes";
    public static final String STATISTIC_SUB_PAUSEDURATION = "pauseduration";
    public static final String STATISTIC_SUB_PAUSETIMES = "pausetimes";
    public static final String STATISTIC_SUB_PLAYTIMEPOINT = "playtimepoint";    
    public static final String STATISTIC_SUB_AD_END_TIME = "adendtime";    


    private Context mContext;
    private BaseUri mUri;
    private int mBufferTimes = 0;
    private long mTotalBufferedTime = 0;
    private long mStartTimeStamp = 0L;
    private int mTimesOfLoading = 0;
    private long mBufferStartTime = 0L;
    private long mBufferEndTime = 0L;
    private int mAdDuration = 0;
    private long mBackgroundDuration = 0;
    private long mBackgroundStartTime = 0;
    private long mBackgroundEndTime = 0;
    private long mPauseStartTime = 0;
    private long mPauseEndTime = 0;
    private long mPauseTimes = 0;
    private long mPauseDuration = 0;
    private long mAdStartTime = 0;
    private long mAdEndTime = 0;
    private long mPlayTimePoint = 0;
    private long mDuration = 0;
    private JSONObject mJsonRoot;
    private JSONArray mJsonArray;

    private static class RecordInfo{
        public long mSubStartTime;			//beginTime
        public long mSubEndTime;			//endTime
        public long mSubAdTime;				//ad
        public long mSubAdEndTime;             //ad endTime
        public long mSubBackgroundTime;	//
        public long mSubBufferTimes;
        public long mSubBufferDuration;
        public long mSubTimesOfLoading;
        public long mSubPauseDuration;
        public long mSubPauseTimes;
        public long mSubPlayTimePoint;
    }

    public Statistics(Context context, BaseUri uri){
        mContext = context;
        mUri = uri;
        mJsonRoot = new JSONObject();
        mJsonArray = new JSONArray();
        resetStatistics();
    }

    public void resetStatistics() {
        mStartTimeStamp = System.currentTimeMillis();
        mBufferTimes = 0;
        mTotalBufferedTime = 0;
        mBufferStartTime = 0;
        mBufferEndTime = 0;
        mTimesOfLoading = 0;
        mPauseStartTime = 0;
        mPauseEndTime = 0;
        mPauseTimes = 0;
        mPauseDuration = 0;
        mAdStartTime = 0;
        mAdEndTime = 0;
        mPlayTimePoint = 0;
    }

    public void onResume(){
        mBackgroundEndTime = System.currentTimeMillis();
        if(mBackgroundStartTime != 0){
            mBackgroundDuration = mBackgroundEndTime - mBackgroundStartTime;	
        }else{
            mBackgroundDuration = 0;
        }
    }

    public RecordInfo computeRecordInfo(){
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.mSubStartTime = mStartTimeStamp;
        recordInfo.mSubEndTime = System.currentTimeMillis();
        recordInfo.mSubBufferDuration = mTotalBufferedTime;
        recordInfo.mSubBackgroundTime = mBackgroundDuration;
        recordInfo.mSubBufferTimes = mBufferTimes;
        recordInfo.mSubPauseTimes = mPauseTimes;
        if(mPauseStartTime > 0 && mPauseEndTime > 0){
            recordInfo.mSubPauseDuration = mPauseDuration + mPauseEndTime - mPauseStartTime;
        }else if(mPauseStartTime > 0 && mPauseEndTime == 0){
            recordInfo.mSubPauseDuration = mPauseDuration + System.currentTimeMillis() - mPauseStartTime;
        }else{
            recordInfo.mSubPauseDuration = 0;
        }

        if(mAdStartTime > 0 && mAdEndTime > 0){
            recordInfo.mSubAdTime = mAdEndTime - mAdStartTime;
        }else if(mAdStartTime > 0 && mAdEndTime == 0){
            recordInfo.mSubAdTime = System.currentTimeMillis() - mAdStartTime;
        }else{
            recordInfo.mSubAdTime = 0;
        }
        recordInfo.mSubAdEndTime = mAdEndTime;
        recordInfo.mSubTimesOfLoading = mTimesOfLoading;
        recordInfo.mSubPlayTimePoint = mPlayTimePoint;
        return recordInfo;
    }

    public void pushLastRecord(){
        RecordInfo recordInfo = computeRecordInfo();
        putRecordInfoToJson(recordInfo);
    }

    public void onPause(){
        mBackgroundStartTime = System.currentTimeMillis();
        pushLastRecord();
        resetStatistics();
    }

    public void onPrepared(boolean isAds){
        mTimesOfLoading++;
        if(!isAds){
            mPlayTimePoint = System.currentTimeMillis();
        }
    }

    public void onDuration(int duration){
        mDuration = duration;
    }

    public void bufferStart(){
        mBufferTimes ++;
        mBufferStartTime = System.currentTimeMillis();
    }

    public void bufferEnd(){
        mBufferEndTime = System.currentTimeMillis();
        mTotalBufferedTime = mTotalBufferedTime + mBufferEndTime - mBufferStartTime;
    }

    public void putRecordInfoToJson(RecordInfo recordInfo){
        JSONObject itemObject = new JSONObject();
        try {
            itemObject.put(STATISTIC_SUB_STARTTIME, recordInfo.mSubStartTime);
            itemObject.put(STATISTIC_SUB_ENDTIME, recordInfo.mSubEndTime);
            itemObject.put(STATISTIC_SUB_BACKGROUNDTIME, recordInfo.mSubBackgroundTime);
            itemObject.put(STATISTIC_SUB_BUFFERTIMES, recordInfo.mSubBufferTimes);
            itemObject.put(STATISTIC_SUB_BUFFERDURATION, recordInfo.mSubBufferDuration);
            itemObject.put(STATISTIC_SUB_ADTIME, recordInfo.mSubAdTime);
            itemObject.put(STATISTIC_SUB_LOADINGTIMES, recordInfo.mSubTimesOfLoading);
            itemObject.put(STATISTIC_SUB_PAUSEDURATION, recordInfo.mSubPauseDuration);
            itemObject.put(STATISTIC_SUB_PAUSETIMES, recordInfo.mSubPauseTimes);
            itemObject.put(STATISTIC_SUB_PLAYTIMEPOINT, recordInfo.mSubPlayTimePoint);
            itemObject.put(STATISTIC_SUB_AD_END_TIME, recordInfo.mSubAdEndTime);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        if(itemObject != null){
            mJsonArray.put(itemObject);
        }
    }

    public String generateStatistics(){
        final String dayDate = DKTimeFormatter.getInstance().longToDayDate(System.currentTimeMillis());
        try {
            mJsonRoot.put(STATISTIC_DATE, dayDate);
            mJsonRoot.put(STATISTIC_MEDIACI, mUri.getCi());
            mJsonRoot.put(STATISTIC_ADDURATION, mAdDuration);
            mJsonRoot.put(STATISTIC_DURATION, mDuration);
            mJsonRoot.put(STATISTIC_OFFLINE, false);
            if(mUri instanceof OnlineUri){
                mJsonRoot.put(STATISTIC_MEDIAID, ((OnlineUri) mUri).getMediaId());
                mJsonRoot.put(STATISTIC_SOURCE, ((OnlineUri) mUri).getSource());
                mJsonRoot.put(STATISTIC_RESOLUTION, ((OnlineUri) mUri).getResolution());
                if(mUri.getUri() != null && "file".equals(mUri.getUri().getScheme())){
                    mJsonRoot.put(STATISTIC_OFFLINE, true);
                }
            }else{
                mJsonRoot.put(STATISTIC_MEDIAID, 0);
                mJsonRoot.put(STATISTIC_SOURCE, -1);
            }
            mJsonRoot.put(STATISTIC_PLAYINFOS, mJsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mJsonRoot.toString();
    }

    public void sendStatistics(){
        String statistics = generateStatistics();
        sendStatistic(mContext, statistics);
    }

    public static void sendStatistic(Context context, String statistics){
        try {
            Intent serviceintent = new Intent("com.miui.video.PlayStatisticsService");
            Bundle bundle = new Bundle();
            bundle.putString(STATISTIC_INFO, statistics);
            serviceintent.putExtras(bundle);
            context.startService(serviceintent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifyAdsStart() {
        mAdStartTime = System.currentTimeMillis();
    }

    @Override
    public void onNotifyAdsEnd() {
        mAdEndTime = System.currentTimeMillis();
    }

    @Override
    public void onAdsDuration(int duration) {
        mAdDuration = duration;
    }

    @Override
    public void onPauseStart() {
        mPauseTimes++;
        if(mPauseEndTime > 0){
            mPauseDuration = mPauseDuration + mPauseEndTime - mPauseStartTime;
        }
        mPauseStartTime = System.currentTimeMillis();
        mPauseEndTime = 0;
    }

    @Override
    public void onPauseEnd() {
        mPauseEndTime = System.currentTimeMillis(); 
    }
}
