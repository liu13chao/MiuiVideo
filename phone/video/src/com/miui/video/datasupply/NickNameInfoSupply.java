package com.miui.video.datasupply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.miui.video.api.DKApi;
import com.miui.video.response.GetNickNameResponse;
import com.miui.video.type.UserNickNameInfo;
import com.miui.video.type.UsersNickNameInfoList;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;



/**
 *@author liuchao
 *
 */

public class NickNameInfoSupply implements Observer{
	
	public static final String TAG = NickNameInfoSupply.class.getName();

	private HashMap<String, UserNickNameInfo> mNickNameInfoMap = new HashMap<String, UserNickNameInfo>(); 
	private ServiceRequest mRequest;
	
	private List<NickNameInfoListener> mListeners = new ArrayList<NickNameInfoListener>();
	
	public void getNickNameInfo(List<String> userIds){
		if(userIds == null) {
			return;
		}
		
		if(mRequest != null) {
			mRequest.cancelRequest();
		}
		
		List<String> requestIds = new ArrayList<String>();
		for(int i = 0; i < userIds.size(); i++) {
			String id = userIds.get(i);
			if(!mNickNameInfoMap.containsKey(id)) {
				requestIds.add(id);
			}
		}
		if(requestIds.size() > 0) {
			mRequest = DKApi.getNickNameInfo(requestIds, this);
		}
	}
	
	public void addListener(NickNameInfoListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(NickNameInfoListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	private void onNickNameInfoDone(boolean isError) {
		for(int i = 0; i < mListeners.size(); i++) {
			NickNameInfoListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onNickNameInfoDone(mNickNameInfoMap, isError);
			}
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if (response instanceof GetNickNameResponse) {
			if(response.isSuccessful()) {
				GetNickNameResponse getNickNameResponse = (GetNickNameResponse) response;
				UsersNickNameInfoList userNickNameInfoList = getNickNameResponse.data;
				if(userNickNameInfoList != null) {
					UserNickNameInfo[] userNickNameInfos = userNickNameInfoList.list;
					if(userNickNameInfos != null) {
						for(int i = 0; i < userNickNameInfos.length; i++){
							UserNickNameInfo userNickNameInfo = userNickNameInfos[i];
							if(userNickNameInfo != null) {
								mNickNameInfoMap.put(String.valueOf(userNickNameInfo.userId), userNickNameInfo);
							}
						}
					}
				}
		        onNickNameInfoDone(false);
			} else {
				onNickNameInfoDone(true);
			}	   
		}
	}

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
		
	}
	
	public interface NickNameInfoListener {
		public void onNickNameInfoDone(HashMap<String, UserNickNameInfo> nickNameInfoMap, boolean isError);
	}
}


