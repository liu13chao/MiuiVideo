/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaInfoContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-25
 */
package com.miui.video.controller.content;

import com.miui.video.type.BaseMediaInfo;

import android.content.Context;

/**
 * @author tianli
 *
 */
public class MediaInfoContentBuilder extends BaseGridContentBuilder {

    public MediaDescProvider mProvider;
    
    public MediaInfoContentBuilder(Context context) {
        super(context);
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
        return true;
    }

    @Override
    public boolean isSubtitleInCenter() {
        return true;
    }

    @Override
    public boolean isNameInCenter() {
        return true;
    }

    @Override
    public boolean isMaskVisible() {
        return true;
    }

}
