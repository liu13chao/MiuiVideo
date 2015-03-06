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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.R;

/**
 *@author xuanmingliu
 *
 */

public class RecentPlayMediaCategoryInfo extends MediaCategoryInfo{
	private static final long serialVersionUID = 2L;
	
	private int   playMediaCount;
//	private int   localMediaCount;
	private long  localMediaSize;

	private List<BaseLocalPlayHistory> localPlayHistoryInfoList;
	
	public RecentPlayMediaCategoryInfo() {
		localMediaCategoryType = MediaCategoryTypeDef.LOCALMEDIA_CATEGORY_PLAYHISTORY;
	}
	
	public void setLocalPlayHistoryInfoList(List<BaseLocalPlayHistory> localPlayHistoryInfoList){
//		this.localPlayHistoryInfoList = filterValidPlayHistory(localPlayHistoryInfoList);
		this.localPlayHistoryInfoList = filterHidePlayHistory(localPlayHistoryInfoList);
		prepareRecentPlayStatisticInfo();
	}
	
//	//过滤掉没有播放历史中没有名字的网络视频
//	private List<BaseLocalPlayHistory> filterValidPlayHistory(List<BaseLocalPlayHistory> localPlayHistoryInfoList)
//	{
//		if( localPlayHistoryInfoList == null)
//			return null;
//		
//		BaseLocalPlayHistory baseLocalPlayHistory = null;
//		List<BaseLocalPlayHistory> filteredLocalPlayHistoryList = new ArrayList<BaseLocalPlayHistory>();
//		for(Iterator<BaseLocalPlayHistory> iter = localPlayHistoryInfoList.iterator(); iter.hasNext();)
//		{
//			baseLocalPlayHistory = iter.next();
//			if( baseLocalPlayHistory.isMediaPlayHistory()) {
//				LocalPlayHistory localPlayHistory = (LocalPlayHistory) baseLocalPlayHistory;
//				if(!localPlayHistory.localVideoHistory && localPlayHistory.videoName.length() == 0)
//					continue;
//			}
//			
//			filteredLocalPlayHistoryList.add(baseLocalPlayHistory);
//			
///*			if(!localPlayHistory.localVideoHistory && localPlayHistory.videoName.length() == 0)
//				continue;
//			else
//				filteredLocalPlayHistoryList.add(localPlayHistory);*/
//		}
//		
//		return filteredLocalPlayHistoryList;
//	}
	
	//过滤掉被隐藏的视频
	private List<BaseLocalPlayHistory> filterHidePlayHistory(List<BaseLocalPlayHistory> localPlayHistoryInfoList) {
		List<BaseLocalPlayHistory> filteredLocalPlayHistoryList = new ArrayList<BaseLocalPlayHistory>();
		if(localPlayHistoryInfoList != null) {
			for(int i = 0; i < localPlayHistoryInfoList.size(); i++) {
				BaseLocalPlayHistory baseLocalPlayHistory = localPlayHistoryInfoList.get(i);
				if(baseLocalPlayHistory instanceof LocalPlayHistory) {
					LocalPlayHistory localPlayHistory = (LocalPlayHistory) baseLocalPlayHistory;
					if(!localPlayHistory.isLocalHide()) {
						filteredLocalPlayHistoryList.add(localPlayHistory);
					}
				}
			}
		}
		return filteredLocalPlayHistoryList;
	}
	
	public List<BaseLocalPlayHistory> getLocalPlayHistoryInfoList() {
		return localPlayHistoryInfoList;
	}

	public BaseLocalPlayHistory getLocalPlayHistyoryInfo(int index) {
		if( index < 0 || index >= localPlayHistoryInfoList.size())
			return null;
		
		return localPlayHistoryInfoList.get(index);
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
	//	return localMediaCount;
		if( localPlayHistoryInfoList == null)
			return 0;
		
        int count = localPlayHistoryInfoList.size();
		return count;
	}

	@Override
	public String getLocalMediaSize() {
		return formatLocalMediaSize(localMediaSize);
	}
	
	public int getMediaCount() {
		return playMediaCount;
	}

	private void prepareRecentPlayStatisticInfo() {
		if( this.localPlayHistoryInfoList != null) {
			playMediaCount = localPlayHistoryInfoList.size();
			int count = localPlayHistoryInfoList.size();
//			localMediaCount = 0;
			localMediaSize = 0;
			for(int i = 0; i < count; i++) {
				BaseLocalPlayHistory baseLocalPlayHistory = localPlayHistoryInfoList.get(i);
				if( baseLocalPlayHistory.isMediaPlayHistory()) {
					LocalPlayHistory localPlayHistory = (LocalPlayHistory) baseLocalPlayHistory;
					if(localPlayHistory.localVideoHistory) {
//						localMediaCount += 1;

						String localPath = localPlayHistory.mediaUrl;
						File file = new File(localPath);
						localMediaSize += file.length();
					}
				}
			}
		}
	}

	@Override
	public String getCategoryDesc() {
/*		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getLocalMediaCount());
		Resources res = DKApp.getR();
		strBuilder.append(res.getString(R.string.ge));
		strBuilder.append(res.getString(R.string.video));
		return strBuilder.toString();*/
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

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setHeadName(char c) {
	}
}


