/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaDescProvider.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-23
 */
package com.miui.video.controller.content;

import android.content.Context;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.local.Favorite;
import com.miui.video.local.PlayHistory;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.InformationData;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;
import com.miui.video.util.TimeUtils;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public abstract class MediaDescProvider {

    public BaseMediaInfo mMedia;
    
    public MediaDescProvider(BaseMediaInfo media){
        mMedia = media;
    }
    
    public abstract String getDesc();
    
    public abstract String getSubtitle();
    
    public static MediaDescProvider getProvider(BaseMediaInfo mediaInfo){
        if(mediaInfo instanceof InformationData){
            return new InfoDataProvider((InformationData)mediaInfo);
        }else if(mediaInfo instanceof TelevisionInfo){
            return new TvInfoProvider((TelevisionInfo)mediaInfo);
        }else if(mediaInfo instanceof MediaInfo){
            return new MediaInfoProvider((MediaInfo)mediaInfo);
        }
        return null;
    }
    
    public static class MediaInfoProvider extends MediaDescProvider{

        public MediaInfo mMedia;
        
        public MediaInfoProvider(MediaInfo media) {
            super(media);
            mMedia = media;
        }

        @Override
        public String getDesc() {
            if(mMedia != null){
                if(mMedia.isMultiSetType()){
                    return MediaViewHelper.getMediaStatus(mMedia);
                }else{
                    Context context = DKApp.getAppContext();
                    if(context != null){
                        return context.getString(R.string.score_by, Util.formatFloat(mMedia.score));
                    }
                }
            }
            return "";
        }

        @Override
        public String getSubtitle() {
            if(mMedia != null){
                return mMedia.shortdesc;
            }
            return "";
        }
    };
    
    public static class InfoDataProvider extends MediaDescProvider{

        public InformationData mMedia;
        
        public InfoDataProvider(InformationData media) {
            super(media);
            mMedia = media;
        }

        @Override
        public String getDesc() {
            if(mMedia != null){
                return TimeUtils.parseShortTime(mMedia.playlength * 1000);
            }
            return "";
        }

        @Override
        public String getSubtitle() {
            return "";
        }
    };
    
    public static class TvInfoProvider extends MediaDescProvider{

        public TelevisionInfo mMedia;
        
        public TvInfoProvider(TelevisionInfo media) {
            super(media);
            mMedia = media;
        }

        @Override
        public String getDesc() {
            return "";
        }

        @Override
        public String getSubtitle() {
            if(mMedia != null){
                TelevisionShow show = mMedia.getCurrentShow();
                if(show != null){
                    return show.videoname;
                }
            }
            return "";
        }
    };
    
    public static class PlayHistoryProvider extends MediaDescProvider{

        public PlayHistory mMedia;
        
        public PlayHistoryProvider(PlayHistory media) {
            super(media);
            mMedia = media;
        }

        @Override
        public String getDesc() {
            return "";
        }

        @Override
        public String getSubtitle() {
            if(mMedia != null){
            }
            return "";
        }
    };
    
    public static class FavoriteProvider extends MediaDescProvider{

        public Favorite mMedia;
        
        public FavoriteProvider(Favorite media) {
            super(media);
            mMedia = media;
        }

        @Override
        public String getDesc() {
            return "";
        }

        @Override
        public String getSubtitle() {
            if(mMedia != null){
            }
            return "";
        }
    };
    
}
