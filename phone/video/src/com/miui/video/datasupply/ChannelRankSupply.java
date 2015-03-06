package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.RankInfoListResponse;
import com.miui.video.type.RankInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class ChannelRankSupply implements Observer {

	private RankInfo[] rankInfos;
	private ArrayList<ChannelRankListener> listeners = new ArrayList<ChannelRankListener>();
	private ServiceRequest request;
	
	private int pageNo = 1;
	private int pageSize = 6;
	
	public void getRankMedias(int channelId, String userBehaveData) {
		resetData();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getRankInfoList(channelId, pageNo, pageSize, userBehaveData, this);
	}
	
	public void addListener(ChannelRankListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(ChannelRankListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			RankInfoListResponse rankInfoListResponse = (RankInfoListResponse) response;
			rankInfos = rankInfoListResponse.data;
			onRankMediasDone(false);
		} else {
			onRankMediasDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void resetData() {
		rankInfos = null;
	}
	
	private void onRankMediasDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			ChannelRankListener listener = listeners.get(i);
			if(listener != null) {
				listener.onRankMediasDone(rankInfos, isError);
			}
		}
	}
	
	//self def class
	public interface ChannelRankListener {
		public void onRankMediasDone(RankInfo[] rankInfos, boolean isError);
	}
}
