/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  RecommendViewClickHandler.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-12
 */
package com.miui.video.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.miui.video.FeatureMediaActivity;
import com.miui.video.MediaDetailActivity;
import com.miui.video.addon.AddonHandler;
import com.miui.video.info.InfoPlayUtil;
import com.miui.video.live.TvPlayManager;
import com.miui.video.type.AddonInfo;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.InformationData;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.TelevisionInfo;

/**
 * @author tianli
 *
 */
public class MediaViewClickHandler implements MediaViewClickListener{
    
    public Activity mActivity;
    public String mSourcePath;
    
    public MediaViewClickHandler(Activity context, String path){
        mActivity = context;
        mSourcePath = path;
    }
    
    @Override
    public void onMediaClick(View view, BaseMediaInfo media){
        if(media instanceof TelevisionInfo) {
            TelevisionInfo televisionInfo = (TelevisionInfo) media;
            TvPlayManager.playChannel(mActivity, televisionInfo, mSourcePath);
        } else if(media instanceof MediaInfo) {
            Intent intent = new Intent();
            intent.setClass(mActivity, MediaDetailActivity.class);
            intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)media);
            intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, mSourcePath);
            mActivity.startActivity(intent);
        } else if(media instanceof SpecialSubject) {
            SpecialSubject specialSubject = (SpecialSubject) media;
            Intent intent = new Intent();
            intent.putExtra(FeatureMediaActivity.KEY_FEATURE, specialSubject);
            intent.putExtra(FeatureMediaActivity.KEY_SOURCE_PATH, mSourcePath);
            intent.setClass(mActivity, FeatureMediaActivity.class);
            mActivity.startActivity(intent);
        } else if (media instanceof AddonInfo) {
            AddonInfo addonInfo = (AddonInfo) media;
            new AddonHandler(mActivity, null).onAddonClick(addonInfo);
        } else if(media instanceof InformationData) {
            InformationData informationData = (InformationData) media;
            InfoPlayUtil.playInformation(mActivity, informationData, mSourcePath);
        }
    }

    @Override
    public void onMediaLongClick(View view, BaseMediaInfo media) {
    }
}
