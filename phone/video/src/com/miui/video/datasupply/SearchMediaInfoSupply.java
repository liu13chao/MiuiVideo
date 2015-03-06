package com.miui.video.datasupply;

import java.util.ArrayList;
import java.util.HashMap;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.api.DKApi;
import com.miui.video.response.SearchMediaInfoResponse;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.CategoryInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SearchInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class SearchMediaInfoSupply implements Observer {

	private HashMap<String, CategoryDetailInfo> categoryDetailInfoMap = new HashMap<String, CategoryDetailInfo>();
	private ArrayList<CategoryDetailInfo> categoryDetailInfos = new ArrayList<CategoryDetailInfo>();
	private ArrayList<BaseMediaInfo> recommends = new ArrayList<BaseMediaInfo>();
	private ArrayList<SearchMediaInfoListener> listeners = new ArrayList<SearchMediaInfoListener>();
	private ServiceRequest request;
	
	private String CATEGORY_All;
	private int PAGE_SIZE = 24;
	private String categoryName;
	
	public void getSearchMediaInfolist(String keyWord, String categoryName, String statisticInfo) {
		this.categoryName = categoryName;
		addCategoryAllToMap();
		
		CategoryDetailInfo categoryDetailInfo = categoryDetailInfoMap.get(categoryName);
		SearchInfo searchInfo = new SearchInfo();
		if(categoryDetailInfo != null) {
			searchInfo.pageNo = categoryDetailInfo.pageNo;
			searchInfo.searchMask = categoryDetailInfo.searchMask;
		} else {
			searchInfo.pageNo = 1;
		}
		searchInfo.mediaName = keyWord;
		searchInfo.pageSize = PAGE_SIZE;
		searchInfo.mediaNameSearchType = DKApi.SEARCH_CHANNEL_SUMMARY_BY_KEYWORD;
		searchInfo.statisticInfo = statisticInfo;
		
		recommends.clear();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.searchMediaInfo(searchInfo, this);
	}
	
	public void addListener(SearchMediaInfoListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(SearchMediaInfoListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			SearchMediaInfoResponse searchMediaInfoResponse = (SearchMediaInfoResponse) response;
			prepareData(searchMediaInfoResponse);
			onSearchMediasDone(false);
		} else {
			onSearchMediasDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void prepareData(SearchMediaInfoResponse searchMediaInfoResponse) {
		if(categoryName.equals(CATEGORY_All)) {
			CategoryInfo[] categoryInfos = searchMediaInfoResponse.categoryinfo;
			if(categoryInfos != null) {
				for(int i = 0; i < categoryInfos.length; i++) {
					if(!categoryDetailInfoMap.containsKey(categoryInfos[i].category)) {
						CategoryDetailInfo categoryDetailInfo = new CategoryDetailInfo();
						categoryDetailInfo.categoryName = categoryInfos[i].category;
						categoryDetailInfo.mediaCount = categoryInfos[i].count;
						categoryDetailInfo.searchMask = categoryInfos[i].searchmask;
						categoryDetailInfoMap.put(categoryDetailInfo.categoryName, categoryDetailInfo);
						categoryDetailInfos.add(categoryDetailInfo);
					}
				}
			}
		}
		
		CategoryDetailInfo categoryDetailInfo = categoryDetailInfoMap.get(categoryName);
		if(categoryDetailInfo != null) {
			categoryDetailInfo.mediaCount = searchMediaInfoResponse.count;
			if(searchMediaInfoResponse.data != null) {
				for(int i = 0; i < searchMediaInfoResponse.data.length; i++) {
					categoryDetailInfo.mediaInfoList.add(searchMediaInfoResponse.data[i]);
				}
				if(searchMediaInfoResponse.data.length < PAGE_SIZE) {
					categoryDetailInfo.canLoadMore = false;
				}
			} else {
				categoryDetailInfo.canLoadMore = false;
			}
			categoryDetailInfo.pageNo++;
		}
		
		if(searchMediaInfoResponse.recommend != null) {
			for(int i = 0; i < searchMediaInfoResponse.recommend.length; i++) {
				if(searchMediaInfoResponse.recommend[i] != null) {
					recommends.add(searchMediaInfoResponse.recommend[i]);
				}
			}
		}
	}
	
	private void onSearchMediasDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			SearchMediaInfoListener listener = listeners.get(i);
			if(listener != null) {
				listener.onSearchMediaInfoDone(categoryDetailInfoMap, categoryDetailInfos, recommends, isError);
			}
		}
	}
	
	private void addCategoryAllToMap() {
		CATEGORY_All = DKApp.getAppContext().getString(R.string.all);
		if(!categoryDetailInfoMap.containsKey(CATEGORY_All)) {
			CategoryDetailInfo categoryDetailInfo = new CategoryDetailInfo();
			categoryDetailInfoMap.put(CATEGORY_All, categoryDetailInfo);
			categoryDetailInfos.add(categoryDetailInfo);
		}
	}

	//self def class
	public interface SearchMediaInfoListener {
		public void onSearchMediaInfoDone(HashMap<String, CategoryDetailInfo> categoryDetailInfoMap, 
				ArrayList<CategoryDetailInfo> categoryDetailInfos, ArrayList<BaseMediaInfo> recommends, boolean isError);
	}
	
	public class CategoryDetailInfo {
		public ArrayList<Object> mediaInfoList = new ArrayList<Object>();  //影片
		public MediaInfo[] recommends;  //推荐
		public String categoryName = CATEGORY_All;
		public int searchMask = DKApi.SEARCH_MASK_ALL;
		public int pageNo = 1;
		public int mediaCount;
		public boolean canLoadMore = true;
	}
}
