package com.miui.video.request;

import com.miui.video.response.GetMediaInfoByH5UrlResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class GetMediaInfoByH5UrlRequest extends TvServiceRequest {
	
	public GetMediaInfoByH5UrlRequest(int mediaSource, String playUrl, String statisticInfo) {
		mPath = "/tvservice/getmediainfobyurl";
		addParam("playurl", playUrl);
		addParam("source", String.valueOf(mediaSource));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new GetMediaInfoByH5UrlResponse();
		}
	}
}
