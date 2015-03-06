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

	private ArrayList<FeatureListListener> mListeners = new ArrayList<FeatureListListener>();
	private ArrayList<SpecialSubject> mFeatureList = new ArrayList<SpecialSubject>();
	private ServiceRequest mRequest;
	
	private int pageSize;
	private boolean canLoadMore = true;
	
	public void getFeatureList(int pageNo, int pageSize, String statisticInfo) {
		this.canLoadMore = true;
		this.pageSize = pageSize;
		if(pageNo == 1) {
			mFeatureList.clear();
		}
		if(mRequest != null) {
			mRequest.cancelRequest();
		}
		mRequest = DKApi.getSpecialSubjectList(pageNo, pageSize, statisticInfo, this);
	}
	
	public void addListener(FeatureListListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(FeatureListListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	private void onFeatureListDone(boolean isError) {
		for(int i = 0; i < mListeners.size(); i++) {
			FeatureListListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onFeatureListDone(mFeatureList, isError, canLoadMore);
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
					mFeatureList.add(specialSubjects[i]);
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
		public void onFeatureListDone(ArrayList<SpecialSubject> featureList, boolean isError, boolean canLoadMore);
	}
	
}
