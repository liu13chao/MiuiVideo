/**
s *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   LocalMedia.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.DKApp;
import com.miui.video.model.AppEnv;

/**
 *@author xuanmingliu
 *
 */

public class LocalMedia extends BaseMediaInfo implements Serializable {
	private static final long serialVersionUID = 2L;
	
	//path
	public String mediaPath;
	//title
	public String mediaTitle;
	//mime type
	public String mediaMimeType;
	//display name
	public String displayName;
	//size
	public long   mediaSize;
	//duration
	public long   mediaDuration;
	//last modified
	public long   mediaLastModified;
	//bucket id
	public String bucketId;
	//bucket displayname
	public String bucketName;
	
	public boolean isMyFavorite;
	public int  localId;
	
	public String getPath(){
		return mediaPath;
	}
	
	public String formatMediaDuration() {
		int mediaDurationSecond = (int) (mediaDuration / 1000);
		int hour = (int) (mediaDurationSecond / 60 / 60);
		int minute = (int) ((mediaDurationSecond - hour * 3600) / 60);
		int second = (int) ((mediaDurationSecond - hour * 3600) % 60);
		StringBuilder strBuilder = new StringBuilder();
		if( hour < 10)
			strBuilder.append("0");
		strBuilder.append(hour);
		strBuilder.append(":");
		if( minute < 10)
			strBuilder.append("0");
		strBuilder.append(minute);
		strBuilder.append(":");
		if( second < 10)
			strBuilder.append("0");
		strBuilder.append(second);
		return strBuilder.toString();
	}
	
	public boolean existsInSdRootDir() {
		String sdExtraRootDir = DKApp.getSingleton(AppEnv.class).getExternalSdCardRoot();
		String sdInnerRootDir = DKApp.getSingleton(AppEnv.class).getInternalSdCardRoot();
		String parentPath = "";
		if(mediaPath.lastIndexOf('/') != -1) {
			parentPath = mediaPath.substring(0, mediaPath.lastIndexOf('/'));
		}
		if(parentPath.equals(sdExtraRootDir) || parentPath.equals(sdInnerRootDir)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getName() {
		if(displayName != null && displayName.length() > 0){
			return displayName;
		}
		return mediaTitle;
	}

	@Override
	public String getDesc() {
		return getDescSouth();
	}
	
	@Override
	public String getDescSouth() {
		return formatMediaDuration();
	}

	@Override
	public ImageUrlInfo getPosterInfo() {
//		if(mThumbnailTaskInfo == null) {
//			if(!Util.isEmpty(mediaPath)) {
//				mThumbnailTaskInfo = new ThumbnailTaskInfo(mediaPath, 3);
//			}
//		}
//		return mThumbnailTaskInfo;
	    return null;
	}

    @Override
    public String getMediaStatus() {
        return "";
    }
    
    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public String getUrl() {
        return mediaPath;
    }
    
}


