/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   MyFavoriteMediaCategoryInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-1-16
 */

package com.miui.video.type;

import java.util.List;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;


/**
 *@author xuanmingliu
 *
 */

public class MyFavoriteMediaCategoryInfo extends MediaCategoryInfo{
	private static final long serialVersionUID = 2L;
	
	private List<LocalMyFavoriteItemInfo> myFavoriteItemInfoList;
	
	public MyFavoriteMediaCategoryInfo() {
		localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_MYFAVORITE;
	}
	
	public void setMyFavoriteInfoList(List<LocalMyFavoriteItemInfo> myFavoriteItemInfoList) {
		this.myFavoriteItemInfoList = myFavoriteItemInfoList;
	}
	
	public List<LocalMyFavoriteItemInfo> getMyFavoriteInfoList() {
		return myFavoriteItemInfoList;
	}

	public LocalMyFavoriteItemInfo getMyFavoriteInfo(int index) {
		if( index < 0 || index >= myFavoriteItemInfoList.size())
			return null;
		
		return myFavoriteItemInfoList.get(index);
	}
	
	@Override
	public String getMediaParentTitle() {
		return "";
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
		return myFavoriteItemInfoList.size();
	}

	@Override
	public String getLocalMediaSize() {
		return formatLocalMediaSize(0);
	}

	@Override
	public String getCategoryDesc() {
		StringBuilder strBuilder = new StringBuilder();
		Resources res = DKApp.getAppContext().getResources();
		String str = res.getString(R.string.count_ge);
		str = String.format(str, getLocalMediaCount());
		strBuilder.append(str);
		strBuilder.append(res.getString(R.string.favorite));
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
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHeadName(char c) {
		// TODO Auto-generated method stub
		
	}
}


