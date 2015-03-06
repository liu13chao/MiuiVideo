package com.miui.video.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.text.TextUtils;

public class TelevisionInfo extends OnlineMediaInfo {

    public static final String TAG = TelevisionInfo.class.getName();
    
    public int source;
    public char headletter = '#';  //电视台首字母, char
    public String videoidentifying;  //电台播放id
    public int epgid;                //佳视互动电台播放id
    public String cmccid;
    public int backgroundcolor;  //海报背景颜色
    public int hotindex;  
    public int channelid;        //和mediaId是同一个东西，和后台约定字段的时候重复了
    public String channelname;   //和mediaName是同一个东西，和后台约定字段的时候重复了
    public TelevisionShow currentprogramme;  //电台当前节目

	private static final long serialVersionUID = 2L;
	
	private ArrayList<TelevisionShow> televisionShowArray = new ArrayList<TelevisionShow>();
	
	public TelevisionInfo() {
	}
	
	public synchronized  void setTelevisionShowDataList(TelevisionShowDataList televisionShowDataList){
		fillTelevisionShowArray(televisionShowDataList);
		updateTelevisionInfo();
	}
	
	public synchronized TelevisionShow getCurrentShow(){
		return currentprogramme;
	}
	
	public List<TelevisionShow> getTelevisionShowArray() {
		return televisionShowArray;
	}
	
	public synchronized void setCurrentShow(TelevisionShow televisionShow) {
		this.currentprogramme = televisionShow;
	}
	
	public synchronized boolean updateTelevisionInfo(){
//		DKLog.w(TAG, "updateTelevisionInfo" + medianame);
		currentprogramme = null;
		long curSysTime = System.currentTimeMillis() / 1000;
		TelevisionShow tvShow  = null;
//		DKLog.w(TAG, "television show size: " +televisionShowArray.size());
		for(Iterator<TelevisionShow> Itr = televisionShowArray.iterator(); Itr.hasNext();) {
			tvShow = Itr.next();
			if( tvShow.videoendtime < curSysTime) { 
//				DKLog.w(TAG, "!current show, show name: " +tvShow.videoname +"show end time: " +tvShow.videoendtime);
				Itr.remove();
			}
			else {
//				DKLog.w(TAG, "current show, show name: " +tvShow.videoname +"show end time: " +tvShow.videoendtime);
				currentprogramme = tvShow;
				return true;
			}
		}
		return false;
	}
	
	public synchronized void clearTvShowArray() {
		televisionShowArray.clear();
	}
	
	private void fillTelevisionShowArray(TelevisionShowDataList televisionShowDataList){
		if( televisionShowDataList == null ||
				          televisionShowDataList.result == null)
			return;
		
		televisionShowArray.clear();
		TelevisionShowData[] televisionShowDatas = televisionShowDataList.result;
		if( televisionShowDatas != null) {
			for(int j = 0; j < televisionShowDatas.length; j++){
				TelevisionShowData televisionShowData = televisionShowDatas[j];
				if( televisionShowData != null){
					TelevisionShow[] televisionShows = televisionShowData.data;
					if( televisionShows != null){
						for(int k = 0; k < televisionShows.length; k++){
							TelevisionShow televisionShow = televisionShows[k];
							if(televisionShow != null){
							televisionShowArray.add(televisionShow);
						}
					}
				}
			}
		}
	}
}

	@Override
	public String getName() {
		return medianame;
	}

	@Override
	public String getDesc() {
		if(currentprogramme != null) {
			return currentprogramme.videoname;
		}
		return "";
	}
	
	public int getChannelId(){
	    if(mediaid <= 0){
	        mediaid = channelid;
	    }
        return mediaid;
	}
	
	public String getChannelName(){
        if(TextUtils.isEmpty(medianame)){
            medianame = channelname;
        }
        return medianame;
    }
	
}
