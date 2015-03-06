package com.miui.video.response;

import com.miui.video.type.CategoryInfo;
import com.miui.video.type.MediaInfo;

public class SearchMediaInfoResponse extends TvServiceResponse {
	
	public int reqno;
	public int count; //影片数量
	public MediaInfo[] data;    //影片
	public CategoryInfo[] categoryinfo;  //分类
	public MediaInfo[] recommend;  //推荐
}
