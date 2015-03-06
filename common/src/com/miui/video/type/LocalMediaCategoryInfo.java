/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   LocalMediaCategoryInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.util.Util;


/**
 *@author xuanmingliu
 *
 */

public class LocalMediaCategoryInfo  extends MediaCategoryInfo {
	
	private static final long serialVersionUID = 2L;
	
	private boolean  existInSdCardRoot;
	private boolean  existInCamera;
	
//	private MediaInfo mediaInfo;
	private boolean  isMyFavorite;
	public char headName;
		
	public List<LocalMedia> mediaInfoList;
	
	public LocalMediaCategoryInfo() {
		localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_OTHERDIR;
		mediaInfoList = new ArrayList<LocalMedia>();
		existInSdCardRoot = false;
		existInCamera = false;
		isMyFavorite = false;
	}
	
	public void addLocalMediaInfo(LocalMedia localMediaInfo) {
		mediaInfoList.add(localMediaInfo);
	}
	
	public void removeLocalMediaInfo(int localMediaPos) {
		if ( localMediaPos < 0 || localMediaPos >= mediaInfoList.size()) {
			return;
		}		
		mediaInfoList.remove(localMediaPos);
	}
	
	public List<LocalMedia> getLocalMediaInfo() {
		return mediaInfoList;
	}
	
	public LocalMedia getLocalMediaInfo(int index) {
		if ( index < 0 || index >= mediaInfoList.size()) {
			return null;
		}		
		return mediaInfoList.get(index);
	}
	
	public boolean isExistInSdCardRoot() {
		return existInSdCardRoot;
	}
		
	public void setExistInSdCardRoot(boolean existInSdCardRoot) {
		this.existInSdCardRoot = existInSdCardRoot;
		if (existInSdCardRoot) {
			localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_SINGLEMEDIA;
		}
	}
	
	public boolean isExistInCamera() {
		return existInCamera;
	}
		
	public void setExistInCamera(boolean existInCamera) {
		this.existInCamera = existInCamera;
		if (existInCamera) {
			localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_CAMERAVIDEO;
		}
	}
	
	@Override
	public String getMediaParentTitle() {
		String parentTitle = null;
		if ( mediaInfoList == null || mediaInfoList.size() == 0) {
			parentTitle = "";
		} else {
			LocalMedia  localMediaInfo = mediaInfoList.get(0);
			parentTitle = localMediaInfo.bucketName;
		}
		return parentTitle;
	}
	
	@Override
	public String getMediaCategoryTitle() {
		String categoryTitle = MediaCategoryTypeDef.getLocalMediaCategoryTitle(localMediaCategoryType);
		if (Util.isEmpty(categoryTitle)) {
			if ( mediaInfoList == null || mediaInfoList.size() == 0) {
				categoryTitle = "";				
			} else {
/*				if ( mediaInfoList.size() <= 1 && !isExistInCamera() && existInSdCardRoot) 
					localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_SINGLEMEDIA;*/
				LocalMedia  localMediaInfo = mediaInfoList.get(0);				
				if (mediaInfoList.size() <= 1 && !isExistInCamera())
					localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_SINGLEMEDIA;
				if ( localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_SINGLEMEDIA) {
					categoryTitle = localMediaInfo.displayName;
				} else if ( localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_OTHERDIR) {
					categoryTitle = localMediaInfo.bucketName;
				}
			}
		}
		
		return categoryTitle;
	}

	@Override
	public int getLocalMediaCount() {
		return mediaInfoList.size();
	}

	@Override
	public String getLocalMediaSize() {
		long sumSize = 0;
		for (LocalMedia localMediaInfo : mediaInfoList) {
			sumSize += localMediaInfo.mediaSize;
		}
		return formatLocalMediaSize(sumSize);
	}

	@Override
	public String getCategoryDesc() {
/*		if ( getLocalMediaCount() <= 1 && !isExistInCamera() && existInSdCardRoot) {
			//time length
			LocalMedia  localMediaInfo = mediaInfoList.get(0);
			DKLog.e("LocalMediaCategoryInfo", " duration :" + localMediaInfo.mediaDuration);
			return localMediaInfo.formatMediaDuration();
		} else {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(getLocalMediaCount());
			Resources res = DKApp.getR();
			strBuilder.append(res.getString(R.string.ge));
			strBuilder.append(res.getString(R.string.video));
			return strBuilder.toString();
		}*/
		
		int localMediaCount = getLocalMediaCount();
		LocalMedia localMediaInfo = null;
		if (localMediaCount > 0) {
			localMediaInfo = mediaInfoList.get(0);
		}
		if ((localMediaCount > 0) && !isExistInCamera() && (localMediaCount <= 1)) {
			return localMediaInfo.formatMediaDuration();
		} else {
			StringBuilder strBuilder = new StringBuilder();
			Resources res = DKApp.getAppContext().getResources();
			String str = res.getString(R.string.count_ge);
			str = String.format(str, getLocalMediaCount());
			strBuilder.append(str);
			strBuilder.append(res.getString(R.string.video));
			return strBuilder.toString();
		}
	}

	@Override
	public boolean isMyFavorite() {
		int localMediaCount = getLocalMediaCount();
		if ( localMediaCount > 0 && localMediaCount <= 1 && !isExistInCamera()) {
			LocalMedia  localMediaInfo = mediaInfoList.get(0);
			return localMediaInfo.isMyFavorite;
		} else
			return isMyFavorite;
	}

	@Override
	public void setIsMyFavorite(boolean setMyFavorite) {
		isMyFavorite = setMyFavorite;
	}
	
	public String getBucketName() {
		int count = getLocalMediaCount();
		if ( count > 1)
			return getMediaCategoryTitle();
		else {
			if ( count == 0) {
				return "";
			}			
			return getLocalMediaInfo(0).mediaPath;
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return getMediaCategoryTitle();
	}

	@Override
	public void setHeadName(char c) {
		// TODO Auto-generated method stub
		headName = c;
	}
}


