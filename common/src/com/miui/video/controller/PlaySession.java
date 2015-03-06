package com.miui.video.controller;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseWebMediaActivity;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.db.DBUtil;
import com.miui.video.local.LocalPlayHistory;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.statistic.UploadStatisticInfoManager;
import com.miui.video.storage.MediaItem;
import com.miui.video.type.InformationData;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.OnlineMediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

public class PlaySession{

    private static final String TAG = PlaySession.class.getName();

    private Context mContext;

    public static final String ACTION_PLAY = "duokan.intent.action.VIDEO_PLAY";
    public static final String ACTION_PLAY_BY_HTML5 = "duokan.intent.action.PLAY_BY_HTML5";

    public static final String KEY_MEDIA_ID = "mediaId";
    public static final String KEY_MEDIA_TITLE = "mediaTitle";

    //	private int ci = 1;
    //	private int clarity = -1;
    //	private int source = -1;

    private PlayHistoryManager mPlayHistoryManager;

    public PlaySession(Context context) {
        mContext = context;
        mPlayHistoryManager = DKApp.getSingleton(PlayHistoryManager.class);
    }

    public void startPlayerShareDevice(MediaItem mediaItem) {
        if(mediaItem == null) {
            return;
        }
        String url = mediaItem.getMediaUrl();
        String videoName = mediaItem.getName();
        if(TextUtils.isEmpty(url)){
            return;
        }
        Uri uri = null;
        if(!url.startsWith("http://")){
            File file = new File(url);
            if(!file.exists()){
                return;
            }
            String fileSchemeFile = "file://";
            String fileSchemeContent = "content://";
            if(url.startsWith(fileSchemeFile) || url.startsWith(fileSchemeContent)) {
                uri = Uri.parse(url);
            } else {
                uri = Uri.fromFile(file);
            }
        } else{
            uri = Uri.parse(url);
        }

        Context context = DKApp.getAppContext();
        Intent intent = new Intent(PlaySession.ACTION_PLAY, uri);
        intent.putExtra(PlaySession.KEY_MEDIA_TITLE, videoName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public boolean startPlayerLocal(LocalMedia localMedia) {
        if(localMedia == null) {
            return false;
        }

        String url = localMedia.mediaPath;
        String videoName = localMedia.mediaTitle;
        if(TextUtils.isEmpty(url)){
            DKLog.e(TAG, "start null url!!!");
            return false;
        }
        Uri uri = null;
        Context context = DKApp.getAppContext();
        if(!url.startsWith("http://")){
            if(!Util.fileExists(context, url)){
                //				AlertMessage.show(context, R.string.file_not_exist);
                return false;
            }
            String fileSchemeFile = "file://";
            String fileSchemeContent = "content://";
            if(url.startsWith(fileSchemeFile) || url.startsWith(fileSchemeContent)) {
                uri = Uri.parse(url);
            } else {
                uri = Uri.fromFile(new File(url));
            }
        } else{
            uri = Uri.parse(url);
        }

        Intent intent = new Intent(PlaySession.ACTION_PLAY, uri);
        if(!TextUtils.isEmpty(videoName)){
            intent.putExtra(PlaySession.KEY_MEDIA_TITLE, videoName);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        PlayHistory playHistory = new LocalPlayHistory(localMedia);
        mPlayHistoryManager.addPlayHistory(playHistory);
        return true;
    }

    public boolean startPlayerOffline(OfflineMedia offlineMedia) {
        if(offlineMedia == null) {
            return false;
        }
        String url = offlineMedia.localPath;
        String videoName = TextUtils.isEmpty(offlineMedia.epName) ? offlineMedia.mediaName : offlineMedia.epName;
        if(TextUtils.isEmpty(url)){
            DKLog.e(TAG, "start null url!!!");
            return false;
        }
        Uri uri = null;
        Context context = DKApp.getAppContext();
        if(!url.startsWith("http://")){
            if(!Util.fileExists(context, url)){
                //				AlertMessage.show(context, R.string.file_not_exist);
                return false;
            }
            String fileScheme = "file://";
            if(url.startsWith(fileScheme)){
                url = url.substring(fileScheme.length(), url.length());
            }
            uri = Uri.fromFile(new File(url));
        }else{
            uri = Uri.parse(url);
        }
        Intent intent = new Intent(PlaySession.ACTION_PLAY, uri);
        if(!TextUtils.isEmpty(videoName)){
            intent.putExtra(PlaySession.KEY_MEDIA_TITLE, videoName);
        }
        intent.putExtra(DBUtil.CURRENT_EPISODE, offlineMedia.episode);
        intent.putExtra(DBUtil.MEDIA_ID, offlineMedia.mediaId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        mPlayHistoryManager.addPlayHistory(offlineMedia);
        return true;
    }

    public void startPlayerOnline(MediaInfo mediaInfo,  MediaUrlInfo urlInfo, 
            MediaSetInfo set, int mediaSetStyle, String sourcePath){
        if(mediaInfo == null || urlInfo == null || set == null){
            return;
        }
        int playType;
        if(MediaUrlInfoListSupply.isNeedSDK(urlInfo)) {
            startPlayerSdkMediaInfo(mediaInfo, set, urlInfo.mediaSource, urlInfo.clarity, 
                    urlInfo.sdkinfo2, urlInfo.sdkdisable,urlInfo.mediaUrl, mediaSetStyle, 
                    MediaConfig.MEDIA_TYPE_LONG);
            playType = MediaConfig.PLAY_TYPE_SDK;
        } else if(urlInfo.isHtml()) {
            startWebMediaActivity(mediaInfo, sourcePath, set.ci, urlInfo.clarity, urlInfo.mediaSource, urlInfo.mediaUrl, 
                    set.videoname, mediaSetStyle, MediaConfig.MEDIA_TYPE_LONG);
            playType = MediaConfig.PLAY_TYPE_HTML5;
        } else{
            startPlayerDirect(mediaInfo, set,  urlInfo.mediaSource, urlInfo.clarity, 
                    urlInfo.mediaUrl, mediaSetStyle, MediaConfig.MEDIA_TYPE_LONG);
            playType = MediaConfig.PLAY_TYPE_DIRECT;
        }
        OnlinePlayHistory playHistory = new OnlinePlayHistory(mediaInfo);
        playHistory.mediaCi = set.ci;
        playHistory.mediaSource = urlInfo.mediaSource;
        mPlayHistoryManager.addPlayHistory(playHistory);
        UploadStatisticInfoManager.uploadPlayStatistic(mediaInfo, set.ci, urlInfo.mediaSource, urlInfo.clarity, 
                MediaConfig.MEDIA_TYPE_LONG, playType, sourcePath);
    }

    public void startPlayerOnlineByWeb(MediaInfo mediaInfo, int ci, int source, int clarity,
            boolean isMultiset, String mediaSetName, int mediaSetStyle, String url, String html5Url) {
        Uri uri = Uri.parse(url);
        String videoName = "";
        int mediaId = 0;
        int setnow = 0;
        if(mediaInfo != null) {
            videoName = mediaInfo.medianame;
            mediaId = mediaInfo.mediaid;
            setnow = mediaInfo.setnow;
        }
        Intent intent = new Intent(ACTION_PLAY, uri);
        intent.putExtra(KEY_MEDIA_TITLE, videoName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DBUtil.MEDIA_ID, mediaId);
        intent.putExtra(DBUtil.AVAILABLE_EPISODE_COUNT, setnow);
        intent.putExtra(DBUtil.CURRENT_EPISODE, ci);
        intent.putExtra(DBUtil.PLAY_INDEX, ci - 1);
        intent.putExtra(DBUtil.MEDIA_CLARITY, clarity);
        intent.putExtra(DBUtil.MEDIA_SET_STYLE, mediaSetStyle);
        intent.putExtra(DBUtil.MEDIA_SET_NAME, mediaSetName);
        intent.putExtra(DBUtil.MULTI_SET, isMultiset);
        intent.putExtra(DBUtil.MEDIA_SOURCE, source);
        intent.putExtra(DBUtil.MEDIA_HTML5_URL, html5Url);
        intent.putExtra(DBUtil.KEY_MEDIA_IS_HTML, true);
        mContext.startActivity(intent);
    }

    public void startPlayerInfoByWeb(InformationData info, String videoUrl, String html5Url) {
        if(info == null || videoUrl == null){
            return;
        }
        Uri uri = Uri.parse(videoUrl);
        Intent intent = new Intent(ACTION_PLAY, uri);
        intent.putExtra(KEY_MEDIA_TITLE, info.medianame);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DBUtil.MEDIA_ID, info.mediaid);
        intent.putExtra(DBUtil.MEDIA_CLARITY, info.resolution);
        intent.putExtra(DBUtil.MEDIA_SOURCE, info.source);
        intent.putExtra(DBUtil.MEDIA_HTML5_URL, html5Url);
        intent.putExtra(DBUtil.KEY_MEDIA_IS_HTML, true);
        mContext.startActivity(intent);
    }

    public void addInfoToHistory(InformationData infoData){
        if(infoData == null){
            return;
        }
        OnlinePlayHistory playHistory = new OnlinePlayHistory(infoData);
        mPlayHistoryManager.addPlayHistory(playHistory);
    }

    private void startPlayerSdkMediaInfo(MediaInfo mediaInfo, MediaSetInfo set, int source, int clarity,
            String sdkinfo, boolean sdkdisable,String html5Url, int mediaSetStyle, int videoType) {
        if(mediaInfo == null){
            return;
        }
        Uri uri = Uri.parse(html5Url);
        String videoName = "";
        if(mediaInfo != null) {
            videoName = mediaInfo.medianame;
        }
        Intent intent = new Intent(ACTION_PLAY, uri);
        intent.putExtra(KEY_MEDIA_TITLE, videoName);
        intent.putExtra(DBUtil.KEY_MEDIA_SDKINFO, sdkinfo);
        intent.putExtra(DBUtil.KEY_MEDIA_SDKDISABLE, sdkdisable);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DBUtil.MEDIA_ID, mediaInfo.mediaid);
        intent.putExtra(DBUtil.AVAILABLE_EPISODE_COUNT,
                mediaInfo.setnow);
        intent.putExtra(DBUtil.MULTI_SET, mediaInfo.isMultiSetType());
        intent.putExtra(DBUtil.MEDIA_POSTER_URL, mediaInfo.posterurl);
        intent.putExtra(DBUtil.CURRENT_EPISODE, set.ci);
        intent.putExtra(DBUtil.MEDIA_CLARITY, clarity);
        intent.putExtra(DBUtil.MEDIA_SOURCE, source);
        intent.putExtra(DBUtil.MEDIA_HTML5_URL, html5Url);
        intent.putExtra(DBUtil.MEDIA_SET_NAME, set.videoname);
        intent.putExtra(DBUtil.VIDEO_TYPE, videoType);
        DKApp.getAppContext().startActivity(intent);
    }

    public void startPlayerInfomation(InformationData informationData) {
        if(informationData == null){
            return;
        }
        if(informationData.playType == MediaConfig.PLAY_TYPE_HTML5){
            startWebMediaActivity(informationData, SourceTagValueDef.PHONE_V6_SHORT_VIDEO_VALUE, 0, 
                    informationData.resolution, informationData.source, informationData.playurl, "",
                    MediaConstantsDef.MEDIA_TYPE_VARIETY, MediaConfig.MEDIA_TYPE_SHORT);
        }else{
            Uri uri = Uri.parse(informationData.playurl);
            Intent intent = new Intent(ACTION_PLAY, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(DBUtil.PLAY_TYPE, informationData.playType);
            intent.putExtra(DBUtil.MEDIA_ID, informationData.mediaid);
            intent.putExtra(DBUtil.MULTI_SET, false);
            intent.putExtra(DBUtil.MEDIA_POSTER_URL, informationData.posterurl);
            intent.putExtra(DBUtil.KEY_MEDIA_TITLE, informationData.medianame);
            intent.putExtra(DBUtil.MEDIA_SOURCE, informationData.source);
            intent.putExtra(DBUtil.VIDEO_TYPE, MediaConfig.MEDIA_TYPE_SHORT);
            DKApp.getAppContext().startActivity(intent);
        }
        addInfoToHistory(informationData);
        UploadStatisticInfoManager.uploadPlayStatistic(informationData, 1, informationData.source, 
                informationData.resolution, MediaConfig.MEDIA_TYPE_SHORT, informationData.playType
                , "");
    }

    private void startPlayerDirect(MediaInfo baseInfo, MediaSetInfo set, int source, int clarity,
            String html5Url, int mediaSetStyle, int videoType) {
        if(baseInfo == null || set == null){
            return;
        }		
        Uri uri = Uri.parse(html5Url);
        Intent intent = new Intent(ACTION_PLAY, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (baseInfo != null) {
            intent.putExtra(DBUtil.MEDIA_ID, baseInfo.mediaid);
            intent.putExtra(DBUtil.AVAILABLE_EPISODE_COUNT, baseInfo.setnow);
            intent.putExtra(DBUtil.MULTI_SET, false);
            intent.putExtra(DBUtil.MEDIA_POSTER_URL, baseInfo.posterurl);
            intent.putExtra(KEY_MEDIA_TITLE, baseInfo.medianame);
        }
        intent.putExtra(DBUtil.CURRENT_EPISODE, set.ci);
        intent.putExtra(DBUtil.MEDIA_SET_NAME, set.videoname);
        intent.putExtra(DBUtil.MEDIA_CLARITY, clarity);
        intent.putExtra(DBUtil.MEDIA_SOURCE, source);
        intent.putExtra(DBUtil.MEDIA_HTML5_URL, html5Url);
        intent.putExtra(DBUtil.MEDIA_SET_STYLE, mediaSetStyle);
        intent.putExtra(DBUtil.VIDEO_TYPE, videoType);
        DKApp.getAppContext().startActivity(intent);
    }


    private void startWebMediaActivity(OnlineMediaInfo mediaInfo, String mSourcePath, 
            int ci, int clarity, int mediaSource, String mediaUrl, String mediaSetName, 
            int mediaSetStyle, int videoType) {
        Intent intent = new Intent( ACTION_PLAY_BY_HTML5);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(mediaInfo instanceof MediaInfo){
            intent.putExtra(BaseWebMediaActivity.KEY_MEDIA_INFO, (MediaInfo)mediaInfo);
            intent.putExtra(BaseWebMediaActivity.KEY_IS_MULTI_SET, ((MediaInfo)mediaInfo)
                    .isMultiSetType());
        }else if(mediaInfo instanceof InformationData){
            intent.putExtra(BaseWebMediaActivity.KEY_INFORMATION_DATA, (InformationData)mediaInfo);
        }
        intent.putExtra(BaseWebMediaActivity.KEY_SOURCE_PATH, mSourcePath);
        intent.putExtra(BaseWebMediaActivity.KEY_CI, ci);
        intent.putExtra(BaseWebMediaActivity.KEY_CLARITY, clarity);
        intent.putExtra(BaseWebMediaActivity.KEY_MEDIA_SET_STYLE, mediaSetStyle);
        intent.putExtra(BaseWebMediaActivity.KEY_MEDIA_SET_NAME, mediaSetName);
        intent.putExtra(BaseWebMediaActivity.KEY_SOURCE, mediaSource);
        intent.putExtra(BaseWebMediaActivity.KEY_URL, formatUrl(mediaUrl));
        mContext.startActivity(intent);
    }

    private String formatUrl(String url) {
        if (!Util.isEmpty(url)) {
            int pos = url.lastIndexOf("http://");
            if (pos >= 0) {
                url = url.substring(pos, url.length());
            }
        }
        return url;
    }
}
