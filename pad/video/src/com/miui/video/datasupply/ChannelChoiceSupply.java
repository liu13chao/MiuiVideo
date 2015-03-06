package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaMidTypeValueDef;
import com.miui.video.response.ChannelRecommendationResponse;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class ChannelChoiceSupply implements Observer {

	private Object[] choiceMedias;
	private ArrayList<ChannelRecommendListener> listeners = new ArrayList<ChannelRecommendListener>();
	private ServiceRequest request;
	
	private int pageNo = 1;
	private int pageSize = 24;
	
	public void getChoiceMedias(int channelId, String userBehaveData) {
		resetData();
		if(request != null) {
			request.cancelRequest();
		}
		MediaInfoQuery q = new MediaInfoQuery();
        q.pageNo = pageNo;
        q.pageSize = pageSize;
        q.ids = new int[1];
        q.ids[0] = channelId;
        q.statisticInfo = userBehaveData; 
        request = DKApi.getChannelRecommendation(q, false, this);
	}
	
	public void addListener(ChannelRecommendListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(ChannelRecommendListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			ChannelRecommendationResponse channelRecommendResponse = (ChannelRecommendationResponse) response;
			if(channelRecommendResponse.data != null && channelRecommendResponse.data.length > 0) {
				ChannelRecommendation channelRecommendation = channelRecommendResponse.data[0];
				prepareData(channelRecommendation);
			}
			onChoiceMediasDone(false);
		} else {
			onChoiceMediasDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void resetData() {
		choiceMedias = null;
	}
	
	private void prepareData(ChannelRecommendation channelRecommendation) {
		if(channelRecommendation != null) {
			if(channelRecommendation.midtype == MediaMidTypeValueDef.MID_TYPE_MEDIA) {
				if(channelRecommendation.mediaInfoList != null) {
					int channelRecommendSize = channelRecommendation.mediaInfoList.length;
					choiceMedias = new Object[channelRecommendSize];
					for(int i = 0; i < channelRecommendSize; i++) {
						choiceMedias[i] = channelRecommendation.mediaInfoList[i];
					}
				}
			}
		}
	}
	
	private void onChoiceMediasDone(boolean isError) {
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				ChannelRecommendListener listener = listeners.get(i);
				if(listener != null) {
					listener.onChoiceMediasDone(choiceMedias, isError);
				}
			}
		}
	}

	//self def class
	public interface ChannelRecommendListener {
		public void onChoiceMediasDone(Object[] recommendations, boolean isError);
	}
}
