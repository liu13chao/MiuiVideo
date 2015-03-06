package com.miui.video.request;

import com.miui.video.response.MediaRecommendResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class MediaRecommendRequest extends TvServiceRequest {
	
	public MediaRecommendRequest(int mediaId) {
		mPath = "/tvservice/getrecommendmedia";
		addParam("mediaid", String.valueOf(mediaId));
		addParam("utime", String.valueOf(0));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new MediaRecommendResponse();
		}
	}
}
