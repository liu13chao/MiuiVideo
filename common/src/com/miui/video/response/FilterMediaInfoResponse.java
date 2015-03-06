package com.miui.video.response;

import com.miui.video.type.MediaInfo;


public class FilterMediaInfoResponse extends TvServiceResponse {
	public int count;  //影片总数
	public int reqno;
	public MediaInfo[] data;
	
	@Override
	public void completeData() {
	}
}
