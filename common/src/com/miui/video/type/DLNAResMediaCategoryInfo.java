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

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.MediaItem;

/**
 *@author xuanmingliu
 *
 */

public class DLNAResMediaCategoryInfo extends MediaCategoryInfo{
	private static final long serialVersionUID = 2L;
	
	private int   mPlayMediaCount;
//	private int   localMediaCount;
	private long  mLocalMediaSize;
	private BaseDevice mDevice;
	
	private List<MediaItem> mLocalDlnaResList = new ArrayList<MediaItem>();
	
	public DLNAResMediaCategoryInfo(BaseDevice device) {
		localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_DLNA;
		mDevice = device;
	}
	
	public void setLocalDlnaInfoList(List<MediaItem> dlnaResList){
		if(dlnaResList != null){
			this.mLocalDlnaResList = dlnaResList;
		}
//		this.localDlnaResList = filterInvalidMediaItem(localDlnaResList);
//		this.localDlnaResList = filterHideVideoMediaItem(localDlnaResList);
	}
/*	
	//过滤掉没有播放历史中没有名字的网络视频
	private List<MediaItem> filterInvalidMediaItem(List<MediaItem> localDlnaResList)
	{
		if( localDlnaResList == null)
			return null;
		
		MediaItem baseMediaItem = null;
		List<MediaItem> filteredMediaItemList = new ArrayList<MediaItem>();
		for(Iterator<MediaItem> iter = localDlnaResList.iterator(); iter.hasNext();)
		{
			baseMediaItem = iter.next();
			if( baseMediaItem.isVideo()) 
				filteredMediaItemList.add(baseMediaItem);
		}
		
		return filteredMediaItemList;
	}
	
	//过滤掉被隐藏的视频
	private List<MediaItem> filterHideVideoMediaItem(List<MediaItem> localDlnaResList) {
		List<MediaItem> filteredMediaItemList = new ArrayList<MediaItem>();
		if(localDlnaResList != null) {
			for(int i = 0; i < localDlnaResList.size(); i++) {
				MediaItem baseMediaItem = localDlnaResList.get(i);
				if(baseMediaItem.isApply())
					filteredMediaItemList.add(baseMediaItem);				
			}
		}
		return filteredMediaItemList;
	}
*/	
	public BaseDevice getDevice(){
		return mDevice;
	}
	
	public List<MediaItem> getLocalDlnaResList() {
		return mLocalDlnaResList;
	}

	public MediaItem getLocalDlnaResInfo(int index) {
		if( index < 0 || index >= mLocalDlnaResList.size())
			return null;
		return mLocalDlnaResList.get(index);
	}
	
	@Override
	public String getMediaParentTitle() {
		return "";
	}
	
	@Override
	public String getMediaCategoryTitle() {
		String categoryTitle = null;
		if(mDevice.getName() != null){
			categoryTitle = mDevice.getName();
		}else{
			categoryTitle = MediaCategoryTypeDef.getLocalMediaCategoryTitle(localMediaCategoryType);
		}
		
		if(categoryTitle == null)
			return "";
		
		return categoryTitle;
	}
	
	@Override
	public int getLocalMediaCount() {
        int count = mLocalDlnaResList.size();
		return count;
	}

	@Override
	public String getLocalMediaSize() {
		return formatLocalMediaSize(mLocalMediaSize);
	}
	
	public int getMediaCount() {
		return mPlayMediaCount;
	}

	@Override
	public String getCategoryDesc() {
		StringBuilder strBuilder = new StringBuilder();
		Resources res = DKApp.getAppContext().getResources();
		String str = res.getString(R.string.gong_count_xiang);
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

	public String getDeviceName() {
		return mDevice.getName();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof DLNAResMediaCategoryInfo && mDevice != null){
			return mDevice.equals(((DLNAResMediaCategoryInfo)o).mDevice);
		}
		return super.equals(o);
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


