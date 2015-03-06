package com.miui.video.response;

import com.miui.video.type.ChannelRecommendation;

public class ChannelRecommendationResponse extends TvServiceResponse {
	public ChannelRecommendation[] data;
	
	@Override
	public void completeData() {
		if(data != null && data.length > 0) {
			for(int i = 0; i < data.length; i++) {
				data[i].completeData();
			}
		}
	}
}
