package com.miui.video.statistic;

import java.util.UUID;

import com.miui.video.api.DKApi;
import com.miui.video.controller.MediaConfig;
import com.miui.video.type.OnlineMediaInfo;
import com.miui.video.util.DKLog;

public class UploadStatisticInfoManager {

	private static final String TAG = UploadStatisticInfoManager.class.getName();
	
    public static void uploadPlayStatistic(OnlineMediaInfo baseInfo, int ci, int source, int clarity
            , int videoType, int playType, String sourcePath){
        OpenMediaStatisticInfo openMediaStatisticInfo = new com.miui.video.statistic.OpenMediaStatisticInfo();
        openMediaStatisticInfo.ci = ci;
        openMediaStatisticInfo.mediaId = baseInfo.mediaid;
        openMediaStatisticInfo.mediaSourceType = source;
        openMediaStatisticInfo.videoType = videoType;
        openMediaStatisticInfo.sourcePath = sourcePath;
        if(playType == MediaConfig.PLAY_TYPE_SDK){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_SDK;
        }else if(playType == MediaConfig.PLAY_TYPE_DIRECT){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_URL;
        }else if(playType == MediaConfig.PLAY_TYPE_HTML5){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_HTML5;
        }
        openMediaStatisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_PLAY;
        DKApi.uploadComUserData(openMediaStatisticInfo.formatToJson());
        DKLog.d(TAG, "uploadPlayStatistic");
    }
    
    public static void uploadChangeEpisodeStatistic(int mediaid, int ci, int source, int clarity
            , int videoType, int playType, String sourcePath){
        OpenMediaStatisticInfo openMediaStatisticInfo = new com.miui.video.statistic.OpenMediaStatisticInfo();
        openMediaStatisticInfo.ci = ci;
        openMediaStatisticInfo.mediaId = mediaid;
        openMediaStatisticInfo.mediaSourceType = source;
        openMediaStatisticInfo.videoType = videoType;
        openMediaStatisticInfo.sourcePath = sourcePath;
        if(playType == MediaConfig.PLAY_TYPE_SDK){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_SDK;
        }else if(playType == MediaConfig.PLAY_TYPE_DIRECT){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_URL;
        }else if(playType == MediaConfig.PLAY_TYPE_HTML5){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_HTML5;
        }
        openMediaStatisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_PLAY;
        DKApi.uploadComUserData(openMediaStatisticInfo.formatToJson());
        DKLog.d(TAG, "uploadChangeEpisodeStatistic");
    }    
    
    public static void uploadStartPlayStatistic(int mediaid, int ci, int source, int clarity
            , int videoType, int playType, long timestamp, boolean isads, UUID uuid){
        OpenMediaStatisticInfo openMediaStatisticInfo = new com.miui.video.statistic.OpenMediaStatisticInfo();
        openMediaStatisticInfo.ci = ci;
        openMediaStatisticInfo.mediaId = mediaid;
        openMediaStatisticInfo.mediaSourceType = source;
        openMediaStatisticInfo.videoType = videoType;
        openMediaStatisticInfo.timestamp = timestamp;
        openMediaStatisticInfo.isAds = isads;
        openMediaStatisticInfo.uuid = uuid;
        if(playType == MediaConfig.PLAY_TYPE_SDK){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_SDK;
        }else if(playType == MediaConfig.PLAY_TYPE_DIRECT){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_URL;
        }else if(playType == MediaConfig.PLAY_TYPE_HTML5){
            openMediaStatisticInfo.playmode = UploadPositionDef.FROM_HTML5;
        }
        openMediaStatisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_START_PLAY;
        DKApi.uploadComUserData(openMediaStatisticInfo.formatToJson());
        DKLog.d(TAG, "uploadStartPlayStatistic");
    }   
    
    public static void uploadStartLiveStatistic(int channelid, String channelname, String tvid
            , int source, String videoidentifying, long timestamp, boolean isads, UUID uuid){
    	LivePlayStatisticInfo openMediaStatisticInfo = new com.miui.video.statistic.LivePlayStatisticInfo();
    	openMediaStatisticInfo.channelid = channelid;
    	openMediaStatisticInfo.channelname = channelname;
    	openMediaStatisticInfo.tvid = tvid;
    	openMediaStatisticInfo.source = source;
    	openMediaStatisticInfo.videoidentifying = videoidentifying;
        openMediaStatisticInfo.timestamp = timestamp;
        openMediaStatisticInfo.isAds = isads;
        openMediaStatisticInfo.uuid = uuid;
        openMediaStatisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_START_LIVE;
        DKApi.uploadComUserData(openMediaStatisticInfo.formatToJson());
        DKLog.d(TAG, "uploadStartLiveStatistic");
    }   
}
