package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaMidTypeValueDef;
import com.miui.video.response.MediaRecommendResponse;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Recommendation;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class MediaRecommendSupply implements Observer {

	private BaseMediaInfo[] mediaRecommendations;
	private ArrayList<MediaRecommendListener> listeners = new ArrayList<MediaRecommendListener>();
	private ServiceRequest request;
	private int requestSize;
	
	public void getMediaRecommend(int mediaId, int size) {
		this.requestSize = size;
		resetData();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getMediaRecommend(mediaId, this);
	}
	
	public void addListener(MediaRecommendListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(MediaRecommendListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			MediaRecommendResponse mediaRecommendResponse = (MediaRecommendResponse) response;
			Recommendation[] recommendations = mediaRecommendResponse.data;
			prepareData(recommendations);
			onMediaRecommendDone(false);
		} else {
			onMediaRecommendDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void resetData() {
		mediaRecommendations = null;
	}
	
	private void prepareData(Recommendation[] recommendations) {
		if(recommendations != null) {
			int mediaRecommendationSize = Math.min(requestSize, recommendations.length);
			mediaRecommendations = new BaseMediaInfo[mediaRecommendationSize];
			for(int i = 0; i < mediaRecommendationSize; i++) {
				if(recommendations[i].midtype == MediaMidTypeValueDef.MID_TYPE_MEDIA) {
					mediaRecommendations[i] = recommendations[i];
				}
			}
		}
	}

	private void onMediaRecommendDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			MediaRecommendListener listener = listeners.get(i);
			if(listener != null) {
				listener.onMediaRecommendDone(mediaRecommendations, isError);
			}
		}
	}

	//self def class
	public interface MediaRecommendListener {
		public void onMediaRecommendDone(BaseMediaInfo[] recommendations, boolean isError);
	}
}
