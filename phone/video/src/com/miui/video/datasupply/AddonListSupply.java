package com.miui.video.datasupply;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.api.DKApi;
import com.miui.video.response.AddonListResponse;
import com.miui.video.type.BaseMediaInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class AddonListSupply implements Observer {

	private List<AddonListListener> listeners = new ArrayList<AddonListListener>();
	private List<BaseMediaInfo> addonList = new ArrayList<BaseMediaInfo>();
	private int totalCount;
	private ServiceRequest request;
	
	private int pageSize = 10;
	private boolean canLoadMore = true;
	
	public void addListener(AddonListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(AddonListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	public void getAddonList(int pageNo, String statisticInfo) {
		this.canLoadMore = true;
		if(pageNo == 1) {
			addonList.clear();
		}
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getAddonList(pageNo, pageSize, statisticInfo, this);
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			AddonListResponse addonListResponse = (AddonListResponse) response;
			totalCount = addonListResponse.totalcount;
			if(addonListResponse.data != null) {
				for(int i = 0; i < addonListResponse.data.length; i++) {
					addonList.add(addonListResponse.data[i]);
				}
				if(addonListResponse.data.length < pageSize) {
					canLoadMore = false;
				}
			} else {
				canLoadMore = false;
			}
			onAddonListDone(false);
		} else {
			onAddonListDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void onAddonListDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			AddonListListener listener = listeners.get(i);
			if(listener != null) {
				listener.onAddonListDone(addonList, totalCount, isError, canLoadMore);
			}
		}
	}
	
	public interface AddonListListener {
		public void onAddonListDone(List<BaseMediaInfo> addonList, int totalCount, boolean isError, boolean canLoadMore);
	}
}
