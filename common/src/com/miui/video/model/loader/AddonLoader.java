package com.miui.video.model.loader;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.response.AddonListResponse;
import com.miui.video.type.AddonInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


/**
 * @author dz
 *
 */
public class AddonLoader extends DataLoader implements Observer {
	
	private int mPageNo = 1;
	private int PAGE_SIZE = 10;
	private String mStatisticInfo = "";
	private AddonInfo[] mAddonInfos;
	
	public AddonLoader(int pageNo, String statisticInfo) {
		mPageNo = pageNo; 
		mStatisticInfo = statisticInfo;
	}
	
	@Override
	public void load() {
		getAddonList();
	}
		
	private void getAddonList() {
		DKApi.getAddonList(mPageNo, PAGE_SIZE, mStatisticInfo, this);
	}
		
	public ArrayList<Object> getAddonInfoList() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		if (mAddonInfos != null) {
			for(int i = 0; i < mAddonInfos.length; i++) {
				if (mAddonInfos[i] != null) {
					arrayList.add(mAddonInfos[i]);
				}
			}
		}
		return arrayList;
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	}

	@Override
	public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
		if (response.isSuccessful()) {
			AddonListResponse myResponse = (AddonListResponse) response;
			mPageNo++;
			mAddonInfos = myResponse.data;
			notifyDataReady();
		} else {
			notifyDataFail();
		}
	}
}