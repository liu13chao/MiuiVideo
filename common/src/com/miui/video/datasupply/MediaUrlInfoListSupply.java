package com.miui.video.datasupply;

import java.util.ArrayList;

import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.controller.MediaConfig;
import com.miui.video.response.MediaUrlInfoListResponse;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author tangfuling
 *
 */

public class MediaUrlInfoListSupply implements Observer {
	
	private ArrayList<MediaUrlInfoListListener> listeners = new ArrayList<MediaUrlInfoListListener>();
	private MediaUrlInfoList mediaUrlInfoList;
	private ArrayList<Integer> sourceList = new ArrayList<Integer>();
	private ServiceRequest request;
	
	public void getMediaUrlInfoList(int mediaID, int ci, int source) {
		if(request != null) {
			request.cancelRequest();
		}
		resetData();
		request = DKApi.getMediaUrlInfoList(mediaID, ci, source, this);
	}
	
	public static MediaUrlInfo filterMediaUrlInfoList(MediaUrlInfoList mediaUrlInfoList, int preferenceSource) {
		return filterMediaUrlInfoList(mediaUrlInfoList, preferenceSource, -1);
	}
	
	public static boolean isNeedSDK(MediaUrlInfo urlInfo){
		if(urlInfo != null && urlInfo.sdkinfo2 != null && 
				Util.playBySdk(urlInfo.sdkinfo2, urlInfo.sdkdisable, urlInfo.mediaSource, MediaConfig.MEDIA_TYPE_LONG)){
			return true;
		}else{
			return false;
		}
	}
	
	public ArrayList<Integer> getSourceList(MediaUrlInfoList mediaUrlInfoList) {
		sourceList.clear();
		if(mediaUrlInfoList != null) {
			for(int i = 0; i < mediaUrlInfoList.urlSuper.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlSuper[i];
				if(mediaUrlInfo != null) {
					int mediaSource = mediaUrlInfo.mediaSource;
					if(mediaSource != 0 && !sourceList.contains(mediaSource)) {
						sourceList.add(mediaSource);
					}
				}
			}
			for(int i = 0; i < mediaUrlInfoList.urlHigh.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlHigh[i];
				if(mediaUrlInfo != null) {
					int mediaSource = mediaUrlInfo.mediaSource;
					if(mediaSource != 0 && !sourceList.contains(mediaSource)) {
						sourceList.add(mediaSource);
					}
				}
			}
			for(int i = 0; i < mediaUrlInfoList.urlNormal.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlNormal[i];
				if(mediaUrlInfo != null) {
					int mediaSource = mediaUrlInfo.mediaSource;
					if(mediaSource != 0 && !sourceList.contains(mediaSource)) {
						sourceList.add(mediaSource);
					}
				}
			}
		}
		return sourceList;
	}
	
	//清晰度优先于源
	public static MediaUrlInfo filterMediaUrlInfoList(MediaUrlInfoList mediaUrlInfoList, int preferenceSource, int clarity) {
		MediaUrlInfo resMediaUrlInfo = null;
		if(mediaUrlInfoList != null) {
			for(int i = 0; i < mediaUrlInfoList.urlHigh.length; i++) {
				MediaUrlInfo mediaUrlInfo = mediaUrlInfoList.urlHigh[i];
				if(mediaUrlInfo != null) {
					mediaUrlInfo.clarity = MediaConstantsDef.CLARITY_HIGH;
					int mediaSource = mediaUrlInfo.mediaSource;
					String mediaUrl = mediaUrlInfo.mediaUrl;
					if((resMediaUrlInfo == null && !Util.isEmpty(mediaUrl))
							|| clarity == MediaConstantsDef.CLARITY_HIGH) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource
							&& (clarity == MediaConstantsDef.CLARITY_HIGH || clarity == -1)) {
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
					if((resMediaUrlInfo == null && !Util.isEmpty(mediaUrl))
							|| clarity == MediaConstantsDef.CLARITY_NORMAL) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource
							&& (clarity == MediaConstantsDef.CLARITY_NORMAL || clarity == -1)) {
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
					if((resMediaUrlInfo == null && !Util.isEmpty(mediaUrl))
							|| clarity == MediaConstantsDef.CLARITY_SUPPER) {
						resMediaUrlInfo = mediaUrlInfo;
					}
					if(!Util.isEmpty(mediaUrl) && mediaSource == preferenceSource
							&& (clarity == MediaConstantsDef.CLARITY_SUPPER || clarity == -1)) {
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
