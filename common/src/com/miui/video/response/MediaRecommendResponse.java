package com.miui.video.response;

import com.miui.video.type.Recommendation;

public class MediaRecommendResponse extends TvServiceResponse {
	
	public Recommendation[] data;
	
	@Override
	public void completeData() {
		if(data != null && data.length > 0) {
			for(int i = 0; i < data.length; i++) {
				data[i].completeData();
			}
		}
	}
}
