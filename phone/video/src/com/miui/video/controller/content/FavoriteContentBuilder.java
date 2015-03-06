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

import com.miui.video.local.Favorite;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class FavoriteContentBuilder extends MixedMediaContentBuilder {

    private Favorite mFavorite;
    
    private BaseMediaInfo mMediaInfo;
    
    private MediaDescProvider mDescProvider = null;
    
    public FavoriteContentBuilder(Context context) {
        super(context);
    }
    
    @Override
    public void setMediaInfo(BaseMediaInfo mediaInfo) {
        super.setMediaInfo(mediaInfo);
        mFavorite = getMediaInfo(Favorite.class);
        if(mediaInfo instanceof Favorite){
            mFavorite = (Favorite)mediaInfo;
            mMediaInfo = mFavorite.getFavoriteItem();
            mDescProvider = MediaDescProvider.getProvider(mMediaInfo);
        }else{
            mFavorite = null;
            mDescProvider = null;
        }
    }

    @Override
    public String getSubtitle() {
//        if(mFavorite != null){
//            return Strings.formatSize(mOfflineMedia.getFileSize());
//        }
        return "";
    }
    
    @Override
    public String getStatus() {
        if(mDescProvider != null){
            return mDescProvider.getDesc();
        }
        return "";
    }

    @Override
    public boolean isStatusInCenter() {
        return true;
    }

    @Override
    public boolean isHorizontalPoster() {
        return false;
    }

    @Override
    public String getStatusExtra() {
        return "";
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
