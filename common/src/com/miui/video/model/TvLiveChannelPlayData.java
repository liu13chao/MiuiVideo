/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   LocalPlayHistoryInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-14
 */

package com.miui.video.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.miui.video.api.DKApi;
import com.miui.video.response.TelevisionProgramAssembleResponse;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TvLiveData;
import com.miui.video.type.TvProgrammeAllDataInfo;
import com.miui.video.type.TvProgrammeAssemble;
import com.miui.video.type.TvProgrammeCategory;
import com.miui.video.type.TvProgrammesAndDate;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 *@author liuchao
 *
 */

public class TvLiveChannelPlayData implements Observer{
	
	public static final String TAG = TvLiveChannelPlayData.class.getName();

	private static TvLiveChannelPlayData  sTvLiveChannelPlayData;
	private ServiceRequest mTvLiveChannelPlayDataRequest = null;
	private ArrayList<UpdateTvLiveChannelPlayDataInterface> updateTvLiveChannelPlayDataInterfaces = new ArrayList<UpdateTvLiveChannelPlayDataInterface>();
	
	private TvLiveData mTvLiveData;
	private DataStore dataStore;
	private MyHandler mHandler;
	private  int MSG_LOAD = 100;
	
	@SuppressLint("UseSparseArrays")
	private TvLiveChannelPlayData(){
		dataStore = DataStore.getInstance();
		mHandler = new MyHandler();
		mTvLiveData = new TvLiveData();
		mTvLiveData.tvinfoMaps = new HashMap <Integer, TelevisionInfo>();
		mTvLiveData.tvProgrammeAllDataInfos = new ArrayList<TvProgrammeAllDataInfo>();
		mTvLiveData.tvSubCategorys = new HashMap<Integer, String>();
	}
	
	public synchronized static TvLiveChannelPlayData getInstance() {	
		if( sTvLiveChannelPlayData == null)
			sTvLiveChannelPlayData = new TvLiveChannelPlayData();		
		return sTvLiveChannelPlayData;
	}
	
	public void loadTvLiveChannelPlayData(){ 
		if(!dataStore.isTvLiveDataExpired()){
			loadPlayDataFromDataStore();
		}else{
			getTelevisionRecommendationChoice();
		}			
	}
	
	public TvLiveData getTvLiveData(){
		return mTvLiveData;
	}
	
	public void getTelevisionRecommendationChoice() {
		if(mTvLiveChannelPlayDataRequest != null) {
			mTvLiveChannelPlayDataRequest.cancelRequest();
		}
		mTvLiveChannelPlayDataRequest = DKApi.getTelevisionProgramsAssemble(this);
	}
	
	private void loadPlayDataFromDataStore(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				TvLiveData data = dataStore.loadTvLiveData();
				mTvLiveData.tvinfoMaps = data.tvinfoMaps;
				mTvLiveData.tvProgrammeAllDataInfos = data.tvProgrammeAllDataInfos;
				mTvLiveData.tvSubCategorys = data.tvSubCategorys;
				mHandler.sendEmptyMessage(MSG_LOAD);
			}
		}).start();
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if (response instanceof TelevisionProgramAssembleResponse) {
			TelevisionProgramAssembleResponse televisionProgramListResponse = (TelevisionProgramAssembleResponse) response;
			TvProgrammeAssemble tvInfoList = televisionProgramListResponse.data;
			if( response.isSuccessful()) {
				mTvLiveData.tvProgrammeAllDataInfos.clear();
				mTvLiveData.tvinfoMaps.clear();
				mTvLiveData.tvSubCategorys.clear();
				TvProgrammeCategory[] tvProgrammeCategory = tvInfoList.subcategory;
		        for(int i = 0; i < tvProgrammeCategory.length; i++){
		        	mTvLiveData.tvSubCategorys.put(tvProgrammeCategory[i].category_id, tvProgrammeCategory[i].name);
		        }		
		        Integer categoryid = tvInfoList.categoryid;
		        String category = tvInfoList.category;
		        mTvLiveData.tvSubCategorys.put(categoryid, category);
		        notifyupdateTvLiveChannelPlayCatogery();
		        
		        TelevisionInfo[] tvChannelInfo = tvInfoList.channelinfos;
		        for(int i = 0; i < tvChannelInfo.length; i++){
		        	mTvLiveData.tvinfoMaps.put(tvChannelInfo[i].getChannelId(), tvChannelInfo[i]);
		        }
		        TelevisionInfo tmpTvChannelInfo = new TelevisionInfo();
				TvProgrammesAndDate[] tvProgrammesAndDates = tvInfoList.programmes;
		        for(int i = 0; i < tvProgrammesAndDates.length; i++){
		        	for(int j = 0; j < tvProgrammesAndDates[i].data.length; j++){
		        		TvProgrammeAllDataInfo tmpTvProgrammeAllDataInfo = new TvProgrammeAllDataInfo();
		        		tmpTvProgrammeAllDataInfo.categoryId = tvProgrammesAndDates[i].data[j].category_id;
		        		tmpTvProgrammeAllDataInfo.channelId = tvProgrammesAndDates[i].data[j].channelid;
		        		tmpTvProgrammeAllDataInfo.endTime = tvProgrammesAndDates[i].data[j].endTime;
		        		tmpTvProgrammeAllDataInfo.episode = tvProgrammesAndDates[i].data[j].episode;
		        		tmpTvProgrammeAllDataInfo.hotIndex = tvProgrammesAndDates[i].data[j].hotindex;
		        		tmpTvProgrammeAllDataInfo.startTime = tvProgrammesAndDates[i].data[j].startTime;
		        		tmpTvProgrammeAllDataInfo.category = tvProgrammesAndDates[i].data[j].category;
		        		tmpTvProgrammeAllDataInfo.formattedName = tvProgrammesAndDates[i].data[j].formatted_name;
		        		tmpTvProgrammeAllDataInfo.videoInfo = tvProgrammesAndDates[i].data[j].videoinfo;
		        		tmpTvProgrammeAllDataInfo.videoName = tvProgrammesAndDates[i].data[j].videoname;
		        		tmpTvChannelInfo = mTvLiveData.tvinfoMaps.get(tmpTvProgrammeAllDataInfo.channelId);
		        		tmpTvProgrammeAllDataInfo.headLetter = tmpTvChannelInfo.headletter;
		        		tmpTvProgrammeAllDataInfo.posterurl = tmpTvChannelInfo.posterurl;
		        		tmpTvProgrammeAllDataInfo.backgroundColor = tmpTvChannelInfo.backgroundcolor;
		        		tmpTvProgrammeAllDataInfo.mediaName = tmpTvChannelInfo.getChannelName();
		        		tmpTvProgrammeAllDataInfo.videoIdentiying = tmpTvChannelInfo.videoidentifying;
		        		mTvLiveData.tvProgrammeAllDataInfos.add(tmpTvProgrammeAllDataInfo);		        		
		        	}
		        }
		        notifyupdateTvLiveChannelPlayData();
		        savePlayDataToDataStore();
			} else {
				notifyTvLiveChannelPlayDataFailed();
			}	   
		}
	}


	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
		
	}

	private void notifyupdateTvLiveChannelPlayCatogery() {
		int count = updateTvLiveChannelPlayDataInterfaces.size();
		for(int i = 0; i < count; i++){
			updateTvLiveChannelPlayDataInterfaces.get(i).onTvLiveChannelPlayCatogeryUpdate();
		}
		
	}
	
	private void notifyupdateTvLiveChannelPlayData() { 	
		int count = updateTvLiveChannelPlayDataInterfaces.size();
		for(int i = 0; i < count; i++){
			updateTvLiveChannelPlayDataInterfaces.get(i).onTvLiveChannelPlayDataUpdate();
		}		
	}
	
	private void savePlayDataToDataStore(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				dataStore.saveTvLiveData(mTvLiveData);
			}
		}).start();
	}
	
	private void notifyTvLiveChannelPlayDataFailed() {
		int count = updateTvLiveChannelPlayDataInterfaces.size();
		for(int i = 0; i < count; i++){
			updateTvLiveChannelPlayDataInterfaces.get(i).onTvLiveChannelPlayDataFailed();
		}
	}

	
	public void registerListeners(UpdateTvLiveChannelPlayDataInterface updateTvLiveChannelPlayDataInterface){
		if( updateTvLiveChannelPlayDataInterface == null ||
				updateTvLiveChannelPlayDataInterfaces.contains(updateTvLiveChannelPlayDataInterface))
			return;
		
		this.updateTvLiveChannelPlayDataInterfaces.add(updateTvLiveChannelPlayDataInterface);
	}
	
	public void unRegisterListeners(UpdateTvLiveChannelPlayDataInterface updateTvLiveChannelPlayDataInterface){
		this.updateTvLiveChannelPlayDataInterfaces.remove(updateTvLiveChannelPlayDataInterface);
	}
	
	public interface UpdateTvLiveChannelPlayDataInterface {
		public void onTvLiveChannelPlayCatogeryUpdate();
		public void onTvLiveChannelPlayDataUpdate();
		public void onTvLiveChannelPlayDataFailed();
	}
	
	private class MyHandler extends Handler{
	     @Override
	     public void handleMessage(Message message) {
	       if(message.what == MSG_LOAD){
	    	   notifyupdateTvLiveChannelPlayCatogery();
	    	   notifyupdateTvLiveChannelPlayData();	   
	       }
	     }
	}
}


