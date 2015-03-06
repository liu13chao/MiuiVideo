package com.miui.video.request;

import com.miui.video.response.CategoryListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class CategoryListRequest extends TvServiceRequest {
	
	public CategoryListRequest(int channelID) {
		mPath = "/tvservice/getrecommendchannel";
		addParam("channelid", String.valueOf(channelID));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new CategoryListResponse();
		}
	}
}
