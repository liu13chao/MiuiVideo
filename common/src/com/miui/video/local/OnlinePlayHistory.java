/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  OnlinePlayHistory.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-30
 */
package com.miui.video.local;

import android.text.TextUtils;

import com.miui.video.controller.MediaConfig;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.InformationData;
import com.miui.video.type.MediaInfo;

/**
 * @author tianli
 *
 */
public class OnlinePlayHistory extends PlayHistory {

    private static final long serialVersionUID = 2L;

    private MediaInfo mMediaInfo;
    private InformationData mInformationData;

    public int    mediaCi;             //上次剧集播放到第几集
    public int    mediaSource;         //历史纪录影片的播放源（搜狐或奇艺）
    public int    quality;             //清晰度 （这个可能不是必须）
    public String html5Page = "";      //播放影片的html5页面
    public String playParameter = "";  //第3方需要的播放参数
    public int    mediaSetType;        //单集 、多集
    public int    mediaSetStyle;       //0 teleplay; 1 varierty
    public String sdkInfo;
    public boolean sdkDisable;
    public int videoType;

    public OnlinePlayHistory() {
    }

    public OnlinePlayHistory(MediaInfo mediaInfo){
        assert(mediaInfo != null);
        mMediaInfo = mediaInfo;
        mediaId = mMediaInfo.mediaid;
    }

    public OnlinePlayHistory(InformationData informationData){
        assert(informationData != null);
        mInformationData = informationData;
        completeInfoData();
    }

    private void completeInfoData(){
        if(mInformationData != null){
            mediaCi = 1;
            mediaId = mInformationData.mediaid;
            mediaSource = mInformationData.source;
            html5Page = mInformationData.playurl;
            sdkInfo = mInformationData.sdkinfo2;
            sdkDisable = mInformationData.sdkdisable;
            videoType = MediaConfig.MEDIA_TYPE_SHORT;
            playType = mInformationData.playType;
            quality = mInformationData.resolution;
        }
    }

    public OnlinePlayHistory(OfflineMedia offlineMedia){
        assert(offlineMedia != null);
        if(offlineMedia.mediaInfo != null){
            mMediaInfo = offlineMedia.mediaInfo;	        
        }else{
            mMediaInfo = new MediaInfo();
            mMediaInfo.mediaid = offlineMedia.mediaId;
        }
        mediaCi = offlineMedia.episode;
        mediaSource = offlineMedia.source;
        mediaId = offlineMedia.mediaId;
        mediaUrl = offlineMedia.localPath;
    }

    @Override
    public boolean equals(Object o) {
        boolean eq = super.equals(o);
        if(eq){
            return true;
        }else{
            if(o != null && o instanceof OnlinePlayHistory){
                OnlinePlayHistory history = (OnlinePlayHistory)o;
                if(history.mediaId > 0) {
                    return history.mediaId == mediaId;
                } else {
                    if(!TextUtils.isEmpty(history.html5Page)){
                        return history.html5Page.equals(html5Page);
                    }
                }
            }
            return false;
        }
    }
    @Override
    public int hashCode() {
        if(mediaId > 0) {
            return (mediaId +"").hashCode();
        }
        if(!TextUtils.isEmpty(html5Page)){
            return html5Page.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public BaseMediaInfo getPlayItem() {
        if(mMediaInfo != null) {
            return mMediaInfo;
        } else if(mInformationData != null) {
            return mInformationData;
        }
        return null;
    }

    @Override
    public void updatePlayerHistory(PlayHistory history) {
        super.updatePlayerHistory(history);
        if(history instanceof OnlinePlayHistory){
            mediaCi = ((OnlinePlayHistory)history).mediaCi;
        }
    }

    @Override
    public ImageUrlInfo getPosterInfo() {
        Object item = getPlayItem();
        if(item instanceof BaseMediaInfo){
            return ((BaseMediaInfo)item).getPosterInfo();
        }
        return null;
    }

}
