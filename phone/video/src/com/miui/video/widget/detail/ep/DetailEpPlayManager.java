package com.miui.video.widget.detail.ep;

import android.content.Context;

import com.miui.video.controller.PlaySession;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.videoplayer.ads.AdsManager;

public class DetailEpPlayManager {

    private Context mContext;
    private int preferenceSource = -1;
    
    //received data
    private MediaInfo mMediaInfo;
    private int mMediaSetStyle;
    private String mSourcePath;
//    private int mCi = -1;
    private int mMediaId = -1;
//    private String mSetName = "";
    private MediaSetInfo mSet;

    //data from net
    private MediaUrlInfoList mMediaUrlInfoList;
    private MediaUrlInfo mMediaUrlInfo;

    //data supply
    private MediaUrlInfoListSupply mMediaUrlInfoListSupply;

    public DetailEpPlayManager(Context context) {
        this.mContext = context;
        init();
    }

    public void playMedia(MediaInfo mediaInfo, int mediaSetStyle, 
            MediaSetInfo set, String sourcePath) {
        if(mediaInfo == null || set == null) {
            return;
        }
        this.mMediaInfo = mediaInfo;
        this.mMediaSetStyle = mediaSetStyle;
        this.mSourcePath = sourcePath;
        if(mMediaId == mediaInfo.mediaid && mSet != null && mSet.ci  == set.ci 
                && mMediaUrlInfo != null && mMediaUrlInfo.mediaSource == preferenceSource) {
            startPlayer();
        } else {
            mMediaId = mediaInfo.mediaid;
            mSet = set;
            mMediaUrlInfoListSupply.getMediaUrlInfoList(mMediaId, mSet.ciidx, preferenceSource);
        }
    }

    //init
    private void init() {
        mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
        mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
    }

    //packaged method
    private void startPlayer() {
        if(mMediaUrlInfo == null || mSet == null){
            return;
        }
        new PlaySession(mContext).startPlayerOnline(mMediaInfo, mMediaUrlInfo,
                mSet, mMediaSetStyle, mSourcePath);
        AdsManager.getInstance(mContext).cacheAdsList(mMediaInfo.mediaid,
                mSet.ci , mMediaUrlInfo.mediaSource);
    }

	public void setPreferenceSource(int source){
		preferenceSource = source;
	}
    
    //data callback
    private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {

        @Override
        public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
                boolean isError) {
            mMediaUrlInfoList = mediaUrlInfoList;
            mMediaUrlInfo = MediaUrlInfoListSupply.filterMediaUrlInfoList(mMediaUrlInfoList, preferenceSource);
            startPlayer();
        }
    };
}
