package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.FilterMediaInfoResponse;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class ChannelFilterSupply implements Observer {

	private ArrayList<MediaInfo> filterMedias = new ArrayList<MediaInfo>();
	private boolean canLoadMore = true;
	private int pageSize;
	
	private ArrayList<ChannelFilterListener> listeners = new ArrayList<ChannelFilterListener>();
	private ServiceRequest request;
	
	public void getFilterMedias(MediaInfoQuery query) {
		if(query != null) {
			this.pageSize = query.pageSize;
			if(query.pageNo == 1) {
				filterMedias.clear();
			}
			
			if(request != null) {
				request.cancelRequest();
			}
			canLoadMore = true;
			request = DKApi.getFilterMediaInfo(query, this);
		}
	}
	
	public void addListener(ChannelFilterListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(ChannelFilterListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			FilterMediaInfoResponse filterMediaInfoResponse = (FilterMediaInfoResponse) response;
			if(filterMediaInfoResponse.data != null) {
				if(filterMediaInfoResponse.data.length < pageSize) {
					canLoadMore = false;
				}
				for(int i = 0; i < filterMediaInfoResponse.data.length; i++) {
					filterMedias.add(filterMediaInfoResponse.data[i]);
				}
			} else {
				canLoadMore = false;
			}
			onFilterMediasDone(false);
		} else {
			onFilterMediasDone(true);
		}
	}
	
	private void onFilterMediasDone(boolean isError) {
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				ChannelFilterListener listener = listeners.get(i);
				if(listener != null) {
					listener.onFilterMediasDone(filterMedias.toArray(), isError, canLoadMore);
				}
			}
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	//self def class
	public interface ChannelFilterListener {
		public void onFilterMediasDone(Object[] filterMedias, boolean isError, boolean canLoadMore);
	}
}
