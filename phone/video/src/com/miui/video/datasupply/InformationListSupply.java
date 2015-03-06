package com.miui.video.datasupply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;

import com.miui.video.api.DKApi;
import com.miui.video.response.InfoRecommendResponse;
import com.miui.video.response.InformationListResponse;
import com.miui.video.type.InformationData;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class InformationListSupply implements Observer {

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, InformationDataListDetail> informationDataListMap 
		= new HashMap<Integer, InformationDataListDetail>();
	private int PAGE_SIZE = 12;
	private int curChannelId;
	
	private ArrayList<InformationDataListListener> listeners = new ArrayList<InformationDataListListener>();
	private ArrayList<InfoRecommendListListener> recommendListeners = new ArrayList<InfoRecommendListListener>();
	private ServiceRequest request;
	
	public void addListener(InformationDataListListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeListener(InformationDataListListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	public void addListener(InfoRecommendListListener listener) {
		if(listener != null && !recommendListeners.contains(listener)) {
			recommendListeners.add(listener);
		}
	}
	
	public void removeListener(InfoRecommendListListener listener) {
		if(listener != null) {
			recommendListeners.remove(listener);
		}
	}
	
	public void getInfoRecommendList(int mediaid, int channelID, String statisticInfo){
		if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getInfoRecommendRequest(mediaid, channelID, statisticInfo, this);
	}
	
	public void getInformationList(int channelId, String statisticInfo) {
		this.curChannelId = channelId;
		InformationDataListDetail informationDataList = informationDataListMap.get(curChannelId);
		MediaInfoQuery query = new MediaInfoQuery();
		if(informationDataList != null) {
			query.pageNo = informationDataList.pageNo;
		} else {
			query.pageNo = 1;
		}
		query.ids = new int[1];
		query.ids[0] = curChannelId;
		query.pageSize = PAGE_SIZE;
		query.orderBy = -1;
        query.statisticInfo = statisticInfo;
        
        if(request != null) {
			request.cancelRequest();
		}
		request = DKApi.getInfomationListRequest(query, this);
	}
	
	@Override
	public void onRequestCompleted(ServiceRequest request,
			ServiceResponse response) {
		if(response.isSuccessful()) {
			if(response instanceof InformationListResponse){
				InformationListResponse informationListResponse = (InformationListResponse) response;
				prepareData(informationListResponse);
				onInformationDataListDone(false);
			}else if(response instanceof InfoRecommendResponse){
				InfoRecommendResponse infoRecommendRespone = (InfoRecommendResponse)response;
				onInfoRecommendListDone(prepareData(infoRecommendRespone), false);
			}
		} else {
			onInformationDataListDone(true);
		}
	}
	
	@Override
	public void onProgressUpdate(ServiceRequest request, int progress) {
	
	}
	
	//packaged method
	private void prepareData(InformationListResponse response) {
		InformationData[] medialist = null;
		if(response.data != null) {
			medialist = response.data.medialist;
		}
		
		InformationDataListDetail informationDataList = informationDataListMap.get(curChannelId);
		if(informationDataList == null) {
			informationDataList = new InformationDataListDetail();
			informationDataListMap.put(curChannelId, informationDataList);
		}
		if(medialist != null) {
			for(int i = 0; i < medialist.length; i++) {
				informationDataList.medialist.add(medialist[i]);
			}
		}
		
		informationDataList.pageNo++;
		informationDataList.canLoadMore = true;
		if(medialist != null && medialist.length < PAGE_SIZE) {
			informationDataList.canLoadMore = false;
		}
	}
	
	private InformationDataListDetail prepareData(InfoRecommendResponse response){
		InformationData[] medialist = null;
		if(response.data != null) {
			medialist = response.data.recmedialist;
		}
		InformationDataListDetail informationDataList = new InformationDataListDetail();
		if(medialist != null) {
			for(int i = 0; i < medialist.length; i++) {
				informationDataList.medialist.add(medialist[i]);
			}
		}
		informationDataList.canLoadMore = false;
		return informationDataList;
	}
	
	private void onInformationDataListDone(boolean isError) {
		for(int i = 0; i < listeners.size(); i++) {
			InformationDataListListener listener = listeners.get(i);
			if(listener != null) {
				listener.onInformationDataListDone(informationDataListMap, isError);
			}
		}
	}
	
	private void onInfoRecommendListDone(InformationDataListDetail infoDetails, boolean isError) {
		for(int i = 0; i < recommendListeners.size(); i++) {
			InfoRecommendListListener listener = recommendListeners.get(i);
			if(listener != null) {
				listener.onInfoRecommendListDone(infoDetails, isError);
			}
		}
	}
	
	public interface InfoRecommendListListener {
		public void onInfoRecommendListDone(InformationDataListDetail infoDetails, boolean isError);
	}
	
	//self def class
	public interface InformationDataListListener {
		public void onInformationDataListDone(HashMap<Integer, InformationDataListDetail> informationDataListMap, 
				boolean isError);
	}
	
	public static class InformationDataListDetail {
		public List<InformationData> medialist = new ArrayList<InformationData>();
		public int pageNo = 1;
		public boolean canLoadMore = true;
	}
	
	public static class InformationDataRecommendedList{
	    public InformationDataListDetail mRecommendedList;
	    public InformationData mInfoData;
    }
}
