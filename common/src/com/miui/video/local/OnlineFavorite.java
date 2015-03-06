/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  OnlineFavorite.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-1
 */
package com.miui.video.local;

import com.miui.video.controller.MediaViewHelper;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.MediaInfo;

/**
 * @author tianli
 *
 */
public class OnlineFavorite extends Favorite {
	
    private static final long serialVersionUID = 1L;

    private MediaInfo mMediaInfo;
	private String mId;
	
	public OnlineFavorite(MediaInfo mediaInfo){
		mMediaInfo = mediaInfo;
		mId = mMediaInfo.mediaid +"";
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public BaseMediaInfo getFavoriteItem(){
		return mMediaInfo;
	}

	public MediaInfo getMediaInfo(){
		return mMediaInfo;
	}

	@Override
	public String getDesc() {
		return MediaViewHelper.getMediaStatus(mMediaInfo);
	}

	@Override
	public String getUrl() {
		return mMediaInfo.posterurl;
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
		if(mMediaInfo != null) {
			return mMediaInfo.getPosterInfo();
		}
		return null;
	}

    @Override
    public String getMediaStatus() {
        return "";
    }
}
