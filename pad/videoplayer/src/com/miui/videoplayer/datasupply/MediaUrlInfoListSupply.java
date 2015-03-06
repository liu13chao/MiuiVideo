package com.miui.videoplayer.datasupply;

import java.util.ArrayList;
import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.response.MediaUrlInfoListResponse;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;

/**
 *@author tangfuling
 *
 */

public class MediaUrlInfoListSupply implements Observer {
	
	private ArrayList<MediaUrlInfoListListener> listeners = new ArrayList<MediaUrlInfoListListener>();
	private MediaUrlInfoList mediaUrlInfoList;
	
	private ServiceRequest request;
	
	public void getMediaUrlInfoList(int mediaID, int ci, int source) {
		if(request != null) {
			request.cancelRequest();
		}
		resetData();
		request = DKApi.getMediaUrlInfoList(mediaID, ci, source, this);
	}
	
	public MediaUrlInfo filterMediaUrlInfoList(MediaUrlInfoList mediaUrlInfoList, int preferenceSource) {
		MediaUrlInfo resMediaUrlInfo = null;
		if(mediaUrlInfoList != null) {
			for(int i = 0; i < mediaUrlInfoList.urlHigh.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlHigh[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = MediaConstantsDef.CLARITY_HIGH;
					int mediaSource = mediaUrlInfo.mediaSource;
					String mediaUrl = mediaUrlInfo.mediaUrl;
					if(resMediaUrlInfo == null && !Util.isEmpty(mediaUrl)) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource) {
						return mediaUrlInfo;
					}
				}
			}
			
			for(int i = 0; i < mediaUrlInfoList.urlNormal.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlNormal[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = MediaConstantsDef.CLARITY_NORMAL;
					int mediaSource = mediaUrlInfo.mediaSource;
					String mediaUrl = mediaUrlInfo.mediaUrl;
					if(resMediaUrlInfo == null && !Util.isEmpty(mediaUrl)) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource) {
						return mediaUrlInfo;
					}
				}
			}
			
			for(int i = 0; i < mediaUrlInfoList.urlSuper.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlSuper[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = MediaConstantsDef.CLARITY_SUPPER;
					int mediaSource = mediaUrlInfo.mediaSource;
					String mediaUrl = mediaUrlInfo.mediaUrl;
					if(resMediaUrlInfo == null && !Util.isEmpty(mediaUrl)) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource) {
						return mediaUrlInfo;
					}
				}
			}
		}
		return resMediaUrlInfo;
	}
	
	public void addListener(MediaUrlInfoListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(MediaUrlInfoListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			MediaUrlInfoListResponse mediaUrlInfoListResponse = (MediaUrlInfoListResponse) response;
			mediaUrlInfoList = mediaUrlInfoListResponse.urlList;
			onMediaUrlInfoListDone(false);
		} else {
			onMediaUrlInfoListDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	private void onMediaUrlInfoListDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			MediaUrlInfoListListener listener = listeners.get(i);
			if(listener != null) {
				listener.onMediaUrlInfoListDone(mediaUrlInfoList, isError);
			}
		}
	}
	
	private void resetData() {
		mediaUrlInfoList = null;
	}
	
	//self def class
	public interface MediaUrlInfoListListener {
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList, boolean isError);
	}
}
