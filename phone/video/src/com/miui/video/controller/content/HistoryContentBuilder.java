/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   HistoryContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-19
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.local.PlayHistory;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.InformationData;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.OnlineMediaInfo;
import com.miui.video.util.TimeUtils;

/**
 * @author tianli
 *
 */
public class HistoryContentBuilder extends MixedMediaContentBuilder {

    private PlayHistory mHistory;
    
    public HistoryContentBuilder(Context context) {
        super(context);
    }
    
    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        if(mediaInfo instanceof PlayHistory){
            mHistory = (PlayHistory)mediaInfo;
        }else{
            mHistory = null;
        }
    }

    @Override
    public String getSubtitle() {
        if(mHistory != null){
                return TimeUtils.parseShortTime((int)mHistory.playPosition);
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
        return "";
    }

    @Override
    public boolean isHorizontalPoster() {
        if(mHistory != null && mHistory.getPlayItem() != null){
            Object obj = mHistory.getPlayItem();
            if(obj instanceof InformationData){
                return true;
            }else if(obj instanceof OnlineMediaInfo){
                return false;
            }else if(obj instanceof LocalMedia){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSubtitleInCenter() {
        return true;
    }

    @Override
    public boolean isMaskVisible() {
        return true;
    }
}
