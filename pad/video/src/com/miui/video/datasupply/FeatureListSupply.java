package com.miui.video.datasupply;

import java.util.ArrayList;
import com.miui.video.api.DKApi;
import com.miui.video.response.SpecialSubjectListResponse;
import com.miui.video.type.SpecialSubject;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class FeatureListSupply implements Observer {

	private ArrayList<FeatureListListener> listeners = new ArrayList<FeatureListListener>();
	private ArrayList<Object> featureList = new ArrayList<Object>();
	private ServiceRequest request;
	
	private int pageSize;
	private boolean canLoadMore = true;
	
	public void getFeatureList(int pageNo, int pageSize, String statisticInfo) {
		this.canLoadMore = true;
		this.pageSize = pageSize;
		if(pageNo == 1) {
			featureList.clear();
		}
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getSpecialSubjectList(pageNo, pageSize, statisticInfo, this);
	}
	
	public void addListener(FeatureListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(FeatureListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	private void onFeatureListDone(boolean isError) {
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				FeatureListListener listener = listeners.get(i);
				if(listener != null) {
					listener.onFeatureListDone(featureList, isError, canLoadMore);
				}
			}
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			SpecialSubjectListResponse specialSubjectListResponse = (SpecialSubjectListResponse) response;
			SpecialSubject[] specialSubjects = specialSubjectListResponse.data;
			if(specialSubjects != null) {
				for(int i = 0; i < specialSubjects.length; i++) {
					featureList.add(specialSubjects[i]);
				}
				if(specialSubjects.length < pageSize) {
					canLoadMore = false;
				}
			} else {
				canLoadMore = false;
			}
			onFeatureListDone(false);
		} else {
			onFeatureListDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}

	public interface FeatureListListener {
		public void onFeatureListDone(ArrayList<Object> featureList, boolean isError, boolean canLoadMore);
	}
	
}
