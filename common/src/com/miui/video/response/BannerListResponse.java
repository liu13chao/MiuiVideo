package com.miui.video.response;

import com.miui.video.type.Banner;
import com.miui.video.type.BannerList;
import com.miui.video.type.TelevisionInfo;

public class BannerListResponse extends TvServiceResponse {
	
	public String[] extradata;   //搜索推荐关键字
	public Banner[] data;     //影片，人物，专题
	public TelevisionInfo[] tvdata;   //直播
	
	public BannerList bannerList = new BannerList();
	
	@Override
	public void completeData() {
		if(data != null) {
			for(int i = 0; i < data.length; i++) {
				data[i].completeData();
			}
		}
		bannerList.searchKeyWords = extradata;
		bannerList.tvInfos = tvdata;
		bannerList.banners = data;
	}
}
