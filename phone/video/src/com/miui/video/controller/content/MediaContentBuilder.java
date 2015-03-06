/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-19
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public abstract class MediaContentBuilder {
    
    protected BaseMediaInfo mMediaInfo;
    protected Context mContext;
    
    public MediaContentBuilder(Context context){
        mContext = context;
    }
    
    public String getName(){
        if(mMediaInfo != null){
            return mMediaInfo.getName();
        }
        return "";
    }
    
    public void setMediaInfo(BaseMediaInfo mediaInfo){
        mMediaInfo = mediaInfo;
    }
    
    public <T extends BaseMediaInfo> T getMediaInfo(Class<T> clazz){
        return Util.dynamicCast(mMediaInfo, clazz);
    }
    
    public abstract boolean isNameInCenter();
    public abstract boolean isSinglelineOK();
    public abstract boolean isMaskVisible();
    
}
