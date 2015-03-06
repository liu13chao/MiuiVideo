package com.miui.video.type;

import java.io.Serializable;

public class TelevisionShowDataList implements Serializable {
	private static final long serialVersionUID = 2L;
	
	public int channelId;
	
	public TelevisionShowData[] result;  //某个频道三天的节目信息
	
	public int getTelevisionId(){
		return channelId;
	}
	
	/*
	public TelevisionShow getCurrentShow(){
		if(televisionShowDatas == null){
			return null;
		}
		if(currentDataIndex > televisionShowDatas.length - 1){
			return null;
		}
		TelevisionShowData televisionShowData = televisionShowDatas[currentDataIndex];
		
		TelevisionShow[] televisionShows = televisionShowData.televisionShows;
		if(televisionShows == null){
			return null;
		}
		if(currentShowIndex > televisionShows.length - 1){
			return null;
		}
		
		return televisionShows[currentShowIndex];
	}
	
	public TelevisionShow getNextShow(){
		if(televisionShowDatas == null){
			return null;
		}
		
		return getCurrentShow();
	}
	*/
}
