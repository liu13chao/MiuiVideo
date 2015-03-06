package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.SpecialSubjectMediaResponse;
import com.miui.video.type.SpecialSubjectMedia;
import com.miui.video.type.SpecialSubjectMediaList;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class FeatureMediaSupply implements Observer {

	private ArrayList<FeatureMediaListener> listeners = new ArrayList<FeatureMediaListener>();
	private ArrayList<Object> featureMedias = new ArrayList<Object>();
	private ServiceRequest request;
	
	public void getFeatureList(int featureId, String statisticInfo) {
		featureMedias.clear();
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getSpecialSubjectMedia(featureId, statisticInfo, this);
	}
	
	public void addListener(FeatureMediaListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(FeatureMediaListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	private void onFeatureMediaDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			FeatureMediaListener listener = listeners.get(i);
			if(listener != null) {
				listener.onFeatureMediaDone(featureMedias, isError);
			}
		}
	}
	
	private void generateFeatureMedias(SpecialSubjectMediaList specialSubjectMediaList) {
		if(specialSubjectMediaList == null) {
			return;
		}
		SpecialSubjectMedia[] specialSubjectMedias = specialSubjectMediaList.medialist;
		if(specialSubjectMedias != null) {
			for(int i = 0; i < specialSubjectMedias.length; i++) {
				SpecialSubjectMedia specialSubjectMedia = specialSubjectMedias[i];
				if(specialSubjectMedia != null) {
					featureMedias.add(specialSubjectMedia);
				}
			}
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			SpecialSubjectMediaResponse specialSubjectMediaResponse = (SpecialSubjectMediaResponse) response;
			generateFeatureMedias(specialSubjectMediaResponse.data);
			onFeatureMediaDone(false);
		} else {
			onFeatureMediaDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	public interface FeatureMediaListener {
		public void onFeatureMediaDone(ArrayList<Object> featureMedias, boolean isError);
	}
}
