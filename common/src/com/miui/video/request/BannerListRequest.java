package com.miui.video.request;

import com.miui.video.response.BannerListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class BannerListRequest extends TvServiceRequest {
	
	public BannerListRequest(int channelID, String statisticInfo) {
		mPath = "/tvservice/getbannermedia";
		addParam("channelid", String.valueOf(channelID));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new BannerListResponse();
		}
	}
}
