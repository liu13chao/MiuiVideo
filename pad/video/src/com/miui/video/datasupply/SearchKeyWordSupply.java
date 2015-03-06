package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.SearchMediaInfoResponse;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SearchInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class SearchKeyWordSupply implements Observer {

	private ArrayList<String> searchKeyWordList = new ArrayList<String>();
	private ArrayList<SearchKeyWordListener> listeners = new ArrayList<SearchKeyWordListener>();
	private ServiceRequest request;
	
	public void getSearchKeyWordlist(SearchInfo searchInfo) {
		if(searchInfo == null) {
			return;
		}
		resetData();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.searchMediaInfo(searchInfo, this);
	}
	
	public void addListener(SearchKeyWordListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(SearchKeyWordListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			SearchMediaInfoResponse searchMediaInfoResponse = (SearchMediaInfoResponse) response;
			MediaInfo[] mediaInfos = searchMediaInfoResponse.data;
			prepareData(mediaInfos);
			onSearchMediasDone(false);
		} else {
			onSearchMediasDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void resetData() {
		searchKeyWordList.clear();
	}
	
	private void prepareData(MediaInfo[] mediaInfos) {
		if(mediaInfos != null && mediaInfos.length > 0) {
			for(int i = 0; i < mediaInfos.length; i++) {
				if(mediaInfos[i] != null && mediaInfos[i].medianame != null 
						&& !isInSearchKeyWordList(mediaInfos[i].medianame)) {
					searchKeyWordList.add(mediaInfos[i].medianame);
				}
			}
		}
	}
	
	private boolean isInSearchKeyWordList(String mediaName) {
		for(String str : searchKeyWordList) {
			if(str != null && str.equals(mediaName)) {
				return true;
			}
		}
		return false;
	}
	
	private void onSearchMediasDone(boolean isError) {
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				SearchKeyWordListener listener = listeners.get(i);
				if(listener != null) {
					listener.onSearchKeyWordDone(searchKeyWordList, isError);
				}
			}
		}
	}

	//self def class
	public interface SearchKeyWordListener {
		public void onSearchKeyWordDone(ArrayList<String> searchKeyWordList, boolean isError);
	}
}
