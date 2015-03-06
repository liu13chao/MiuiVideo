/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   MediaCategoryInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.util.OrderUtil.NameComparable;


/**
 *@author xuanmingliu
 *
 */

public abstract class MediaCategoryInfo implements Serializable, NameComparable {
	private static final long serialVersionUID = 2L;
	
	//category type
	protected int localMediaCategoryType = 
			MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_UNKNOWN;
	
	public int  getlocalMediaCategoryType(){
		return localMediaCategoryType;
	}
	//parent file name;
	public abstract String getMediaParentTitle();
	//category title
	public abstract String getMediaCategoryTitle();
	//local media count
	public abstract int getLocalMediaCount();
	//local media size
	public abstract String  getLocalMediaSize();
	//local category description
	public abstract String  getCategoryDesc();
	//my favorite
	public abstract boolean isMyFavorite();
	public abstract void setIsMyFavorite(boolean setMyFavorite);
	
	public boolean isSelectable(){
		if (localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_MYFAVORITE || 
				localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_CAMERAVIDEO || 
				localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_PLAYHISTORY || 
				localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_DLNA|| 
				localMediaCategoryType == MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_DLNA_LIST){
			return false;
		}
		return true;
	}
	
	protected String formatLocalMediaSize(long categoryMediaSize) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;
		String formatSize = null;
		if(categoryMediaSize < kb) {
			formatSize = String.format("%d B", categoryMediaSize);
		} else if(categoryMediaSize < mb) {
			formatSize = String.format("%.2f KB", categoryMediaSize/kb);
		} else if( categoryMediaSize < gb) {
			formatSize = String.format("%.2f MB", categoryMediaSize/mb);
		} else {
			formatSize = String.format("%.2f GB", categoryMediaSize/gb);
		}
		
		return formatSize;
	}
}


