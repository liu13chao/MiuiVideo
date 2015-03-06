package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.util.StringUtils;
import com.miui.video.util.TimeUtils;

public class LocalContentBuilder extends BaseGridContentBuilder{
    
   private LocalMedia mLocalMeida;
   private LocalMediaList mLocalMediaList;
   
    public LocalContentBuilder(Context context) {
        super(context);
    }

    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        mLocalMeida = getMediaInfo(LocalMedia.class);
        mLocalMediaList = getMediaInfo(LocalMediaList.class);
    }

    @Override
    public String getSubtitle() {
        return "";
    }
    
    @Override
    public String getStatus() {
        if(mLocalMeida != null){
            return getVideoDuration(mLocalMeida);
        }else if(mLocalMediaList != null){
            return getVideoDuration(mLocalMediaList.get(0));
        }
        return "";
    }
    
    private String getVideoDuration(LocalMedia media){
        if(media != null){
            return TimeUtils.parseShortTime((int)media.mediaDuration);
        }
        return "";
    }
    
    private String getVideoCount(){
        String format = mContext.getResources().getString(R.string.video_count);
        if(mLocalMediaList != null){
            return StringUtils.formatString(format, mLocalMediaList.size());
        }else{
            return "";
        }
    }

    @Override
    public String getStatusExtra() {
        if(mLocalMediaList != null && mLocalMediaList.size() > 1){
            return getVideoCount();
        }
        return "";
    }

    @Override
    public boolean isStatusInCenter() {
        return false;
    }

    @Override
    public boolean isSubtitleInCenter() {
        return false;
    }

    @Override
    public boolean isNameInCenter() {
        return false;
    }

    @Override
    public boolean isMaskVisible() {
        return true;
    }

}
