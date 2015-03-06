/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   RecentPlayMediaCategoryInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;

/**
 *@author xuanmingliu
 *
 */

public class DLNAListMediaCategoryInfo extends MediaCategoryInfo implements Serializable{
	private static final long serialVersionUID = 2L;
	
	private ArrayList<DLNAResMediaCategoryInfo>  mDLNACategoryInfoList;
	
	public DLNAListMediaCategoryInfo(ArrayList<DLNAResMediaCategoryInfo> categoryInfoList) {
		localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_DLNA_LIST;
		mDLNACategoryInfoList = categoryInfoList;
	}

	public ArrayList<DLNAResMediaCategoryInfo> getDLNACategoryInfoList(){
		return mDLNACategoryInfoList;
	}

	@Override
	public String getMediaCategoryTitle() {	
		String categoryTitle = MediaCategoryTypeDef.getLocalMediaCategoryTitle(localMediaCategoryType);
		if(categoryTitle == null)
			return "";		
		return categoryTitle;
	}
	
	@Override
	public int getLocalMediaCount() {
        int count = mDLNACategoryInfoList.size();
		return count;
	}


	@Override
	public String getCategoryDesc() {
		StringBuilder strBuilder = new StringBuilder();
		Resources res = DKApp.getAppContext().getResources();
		String str = res.getString(R.string.gong_count_dlna);
		str = String.format(str, getLocalMediaCount());
		strBuilder.append(str);
		return strBuilder.toString();
	}

	@Override
	public boolean isMyFavorite() {
		return false;
	}

	@Override
	public void setIsMyFavorite(boolean setMyFavorite) {	
	}


	@Override
	public String getMediaParentTitle() {
		String categoryTitle = MediaCategoryTypeDef.getLocalMediaCategoryTitle(localMediaCategoryType);
		if(categoryTitle == null)
			return "";		
		return categoryTitle;
	}


	@Override
	public String getLocalMediaSize() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setHeadName(char c) {
	}
	
	
	
}


