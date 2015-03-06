package com.miui.video.tv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.miui.video.api.DKApi;
import com.miui.video.model.AppSingleton;
import com.miui.video.model.TelevisionUtil;
import com.miui.video.response.TelevisionShowInfoResponse;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShowDataList;
import com.miui.video.util.DKLog;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * 
 * @author tangfuling
 * 提供直播频道的EPG信息
 *
 */

public class TvEpgManager extends AppSingleton implements Observer {
	
	private static final String TAG = TvEpgManager.class.getSimpleName();
	private ArrayList<TelevisionUpdateInterface> televisionUpdateInterfaces = new ArrayList<TelevisionUpdateInterface>();

	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, TelevisionInfo> tvShowDataInfosMap
	                      = new HashMap<Integer, TelevisionInfo>();
	
	private static final long INTERVAL_TIME =  1 * 60 * 1000;
	
	private boolean mCanAccessNet = true;
	
	public TvEpgManager(Context context) {
		super(context);
	}
	
	//public method
	public void addListener(TelevisionUpdateInterface televisionUpdateInterface){
		if(televisionUpdateInterface != null && 
				!this.televisionUpdateInterfaces.contains(televisionUpdateInterface)) {
			this.televisionUpdateInterfaces.add(televisionUpdateInterface);
		}
	}
	
	public void removeListeners(TelevisionUpdateInterface televisionUpdateInterface){
		this.televisionUpdateInterfaces.remove(televisionUpdateInterface);
	}
	
	public TelevisionInfo getTelevisionInfo(int tvId) {
		return tvShowDataInfosMap.get(tvId);
	}
	
	public void addTelevisionInfo(TelevisionInfo tvInfo){
		if(tvInfo == null) {
			return;
		}
		
		int tvId = tvInfo.mediaid;
		TelevisionInfo televisionInfo = tvShowDataInfosMap.get(tvId);
		if(televisionInfo == null) {
			televisionInfo = tvInfo;
		   tvShowDataInfosMap.put(tvId, televisionInfo);
		}
		
		startUpdateTvShowTask();
	}
	
	public void addTelevisionInfo(List<TelevisionInfo> tvInfos) {
		if(tvInfos == null || tvInfos.size() == 0) {
			return;
		}
		
		for(int i = 0; i < tvInfos.size(); i++){
			TelevisionInfo tvInfo = tvInfos.get(i);
			if(tvInfo != null) {
				int tvId = tvInfo.mediaid;
				TelevisionInfo televisionInfo = tvShowDataInfosMap.get(tvId);
				if( televisionInfo == null) {
					televisionInfo = tvInfo;
					tvShowDataInfosMap.put(tvId, tvInfo);
				}
			}
		}
		
		startUpdateTvShowTask();
	}
	
	public void addTelevisionInfo(TelevisionInfo[] tvInfos){
		if(tvInfos == null || tvInfos.length == 0) {
			return;
		}

		for(int i = 0; i < tvInfos.length; i++){
			if(tvInfos[i] != null) {
				int tvId = tvInfos[i].mediaid;
				TelevisionInfo televisionInfo = tvShowDataInfosMap.get(tvId);
				if( televisionInfo == null) {
					televisionInfo = tvInfos[i];
					tvShowDataInfosMap.put(tvId, televisionInfo);
				}
			}
		}
		
		startUpdateTvShowTask();
	}
	
	public void enableAccessNet() {
		mCanAccessNet = true;
	}
	
	public void disableAccessNet() {
		mCanAccessNet = false;
	}
	
	//get data
	private ServiceRequest getTelevisionShowInfo(int[] tvIds) {  
		if( tvIds == null || !mCanAccessNet)
			return null;
		MediaInfoQuery q = new MediaInfoQuery();
		q.pageNo = 1;
		q.pageSize = -1;
		q.ids = tvIds;
		return DKApi.getTelevisionShowInfo(q, "", this);
    }

	//packaged method
	@SuppressLint("UseSparseArrays")
	private void notifyUpdateTelevision(){ 
		for(int i = 0; i < televisionUpdateInterfaces.size(); i++){
			TelevisionUpdateInterface tvUpdateInterface = televisionUpdateInterfaces.get(i);
			if(tvUpdateInterface != null) {
				tvUpdateInterface.updateTelevision();
			}
		}
	}
	
	private void updateExpiredTelevisionShow(){
		List<Integer> expiredTvIds = new ArrayList<Integer>();
		Set<Integer> keySet = tvShowDataInfosMap.keySet();
		for(Iterator<Integer> iterator = keySet.iterator(); iterator.hasNext();){
			int key = iterator.next();
			TelevisionInfo tvInfo = tvShowDataInfosMap.get(key);
			if( TelevisionUtil.isTelevisionShowExpired(tvInfo)){
				expiredTvIds.add(key);
			}
		}
		
		if(expiredTvIds.size() > 0) {
			List<Integer> tvIdsToRequestList = new ArrayList<Integer>(); 
			for(int i = 0; i < expiredTvIds.size(); i++) {
				int tvId = expiredTvIds.get(i);
				TelevisionInfo televisionInfo = tvShowDataInfosMap.get(tvId);
				if(televisionInfo != null) {
					televisionInfo.updateTelevisionInfo();
					if(televisionInfo.getCurrentShow() == null) {
						tvIdsToRequestList.add(tvId);
					}
				}
			}
			
			//fetch from network
			int count = tvIdsToRequestList.size();
			if( count > 0) {
				int[] tvIdsRequest = new int[count];
				for(int i = 0; i < count; i++) {
					tvIdsRequest[i] = tvIdsToRequestList.get(i);
				}
				getTelevisionShowInfo(tvIdsRequest);
			} else {
				notifyUpdateTelevision();
			}
		}
	}
	
	private void startUpdateTvShowTask() {
		mHandler.postDelayed(mUpdateExpiredTvShowRunnable, 0);
	}
	
	//data callback
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()){
			if(response instanceof TelevisionShowInfoResponse){
				if( response.isSuccessful()) {
					TelevisionShowInfoResponse televisionShowInfoResponse = (TelevisionShowInfoResponse) response;
					TelevisionShowDataList[] televisionShowDataLists = televisionShowInfoResponse.data;
					if(televisionShowDataLists != null){
						int showInfosCount = televisionShowDataLists.length;
						for(int i = 0; i < showInfosCount; i++){
							TelevisionShowDataList televisionShowDataList = televisionShowDataLists[i];
							int curTVId = televisionShowDataList.getTelevisionId();
							DKLog.e(TAG, "onRequestCompleted tvid : " + curTVId);
							TelevisionInfo televisionInfo = tvShowDataInfosMap.get(curTVId);
							if(televisionInfo != null) {
								televisionInfo.setTelevisionShowDataList(televisionShowDataList);
							}
						}
						notifyUpdateTelevision();
					}
				}
			} 
		}
	}

	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
		
	}
	
	//timer task
	private Handler mHandler = new Handler();
	private Runnable mUpdateExpiredTvShowRunnable = new Runnable() {
		
		@Override
		public void run() {
			updateExpiredTelevisionShow();
			mHandler.postDelayed(mUpdateExpiredTvShowRunnable, INTERVAL_TIME);
		}
	};
	
	//self def class
	public interface TelevisionUpdateInterface {
		public void updateTelevision();
	}
}
