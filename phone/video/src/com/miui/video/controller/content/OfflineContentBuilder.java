/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflineContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-19
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.R;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.type.BaseMediaInfo;
import com.xiaomi.common.util.Strings;

/**
 * @author tianli
 *
 */
public class OfflineContentBuilder extends MixedMediaContentBuilder {

    private OfflineMediaList mOfflineMedia;
    
    public OfflineContentBuilder(Context context) {
        super(context);
    }
    
    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        mOfflineMedia = getMediaInfo(OfflineMediaList.class);
    }

    @Override
    public String getSubtitle() {
        if(mOfflineMedia != null){
            return Strings.formatSize(mOfflineMedia.getFileSize());
        }
        return "";
    }
    
    @Override
    public String getStatus() {
        return "";
    }

    @Override
    public boolean isStatusInCenter() {
        return false;
    }

    @Override
    public String getStatusExtra() {
        if (mOfflineMedia.size() > 1) {
            return mContext.getResources().getString(R.string.count_ge_media, mOfflineMedia.size());
        }
        return "";
    }

    @Override
    public boolean isHorizontalPoster() {
        if(mOfflineMedia != null && mOfflineMedia.getPosterInfo() == null){
            return true;
        }
        return false;
    }

    @Override
    public boolean isSubtitleInCenter() {
        return true;
    }

    @Override
    public boolean isMaskVisible() {
        return false;
    }
}
