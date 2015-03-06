package com.miui.video.live;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.TelevisionRecommendResponse;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.TelevisionInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * 
 * @author tangfuling
 * 提供直播频道列表信息
 *
 */
public class TvChannelManager implements Observer {

	private ArrayList<TelevisionInfo> televisionInfos = new ArrayList<TelevisionInfo>();
	private ArrayList<TelevisionInfoListener> listeners = new ArrayList<TelevisionInfoListener>();
	
	private int PAGE_SIZE = 24;
	private boolean canLoadMore = true;

	private ServiceRequest request;
	
	public void getTelevisionInfo(int pageNo) {
		this.canLoadMore = true;
		if(pageNo == 1) {
			televisionInfos.clear();
		}
		
		if(request != null) {
			request.cancelRequest();
		}
		MediaInfoQuery q = new MediaInfoQuery();
        q.pageNo = pageNo;
        q.pageSize = PAGE_SIZE;
        q.ids = new int[1];
        q.ids[0] = -1;
        q.orderBy = DKApi.ORDER_BY_HOT_ASC;
        request = DKApi.getTelevisionRecommendation(q, this);
	}
	
	public void addListener(TelevisionInfoListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(TelevisionInfoListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			TelevisionRecommendResponse televisionRecommendResponse = (TelevisionRecommendResponse) response;
			if(televisionRecommendResponse.data != null) {
				for(int i = 0; i < televisionRecommendResponse.data.length; i++) {
					televisionInfos.add(televisionRecommendResponse.data[i]);
				}
			}
			if(televisionRecommendResponse.data == null || televisionRecommendResponse.data.length < PAGE_SIZE) {
				canLoadMore = false;
			}
			onTelevisionInfosDone(false);
		} else {
			onTelevisionInfosDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void onTelevisionInfosDone(boolean isError) {
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				TelevisionInfoListener listener = listeners.get(i);
				if(listener != null) {
					listener.onTelevisionInfosDone(televisionInfos, isError, canLoadMore);
				}
			}
		}
	}
	
	//self def class
	public interface TelevisionInfoListener {
		public void onTelevisionInfosDone(ArrayList<TelevisionInfo> televisionInfos, boolean isError, boolean canLoadMore);
	}
}
