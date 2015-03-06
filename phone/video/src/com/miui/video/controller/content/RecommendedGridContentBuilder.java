/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendedGridContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.controller.UIConfig;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.InformationData;
import com.miui.video.type.TelevisionInfo;

/**
 * @author tianli
 *
 */
public class RecommendedGridContentBuilder extends BaseGridContentBuilder {

    public ChannelRecommendation mRecommendation;
    public MediaDescProvider mProvider;
    
    public RecommendedGridContentBuilder(Context context, ChannelRecommendation recommendation) {
        super(context);
        mRecommendation = recommendation;
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        mProvider = MediaDescProvider.getProvider(mediaInfo);
    }

    @Override
    public String getSubtitle() {
        if(mProvider != null){
            return mProvider.getSubtitle();
        }
        return null;
    }

    @Override
    public String getStatus() {
        if(mProvider != null){
            return mProvider.getDesc();
        }
        return null;
    }

    @Override
    public String getStatusExtra() {
        return "";
    }

    @Override
    public boolean isStatusInCenter() {
        if(mRecommendation != null && ( mRecommendation.listtype == UIConfig.LIST_TYPE_H
                || mMediaInfo instanceof InformationData)){
            return false;
        }
        return true;
    }

    @Override
    public boolean isSubtitleInCenter() {
        if(mRecommendation != null && ( mRecommendation.listtype == UIConfig.LIST_TYPE_H
                || mMediaInfo instanceof InformationData)){
            return false;
        }
        return true;
    }

    @Override
    public boolean isNameInCenter() {
        if(mRecommendation != null && ( mRecommendation.listtype == UIConfig.LIST_TYPE_H
                || mMediaInfo instanceof InformationData)){
            return false;
        }
        return true;
    }

    @Override
    public boolean isMaskVisible() {
        if(mMediaInfo instanceof TelevisionInfo){
            return false;
        }
        return true;
    }

    @Override
    public boolean isSinglelineOK() {
        return !isNameInCenter();
    }

}
