/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  RequestGroup.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-7
 */
package com.miui.video.helper;

import com.miui.video.request.TvServiceRequest;

public class RequestGroup extends Group<TvServiceRequest> implements LifeCycle{

	public RequestGroup(){
	}
	
	@Override
	public void onCreate() {
	}

	@Override
	public void onStart() {
		for(TvServiceRequest request : mItems){
			request.setShowResultDesc(true);
		}
	}

	@Override
	public void onStop() {
		for(TvServiceRequest request : mItems){
			request.setShowResultDesc(false);
		}
	}

	@Override
	public void onDestroy() {
		for(TvServiceRequest request : mItems){
			request.cancelRequest();
		}
	}
}
