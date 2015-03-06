/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SharedDeviceContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-21
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.R;
import com.miui.video.storage.BaseDevice;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class SharedDeviceContentBuilder extends BaseGridContentBuilder {

    BaseDevice mDevice;
    
    public SharedDeviceContentBuilder(Context context) {
        super(context);
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(mediaInfo instanceof BaseDevice){
            mDevice = (BaseDevice)mediaInfo;
        }else{
            mDevice = null;
        }
    }

    @Override
    public String getSubtitle() {
        if(mDevice != null){
            return mContext.getString(R.string.video_count, 
                    mDevice.getVideoSize());
        }
        return "";
    }
    
    @Override
    public String getStatus() {
        return "";
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
        return false;
    }

}
