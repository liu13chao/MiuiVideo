package com.miui.video.request;

import com.miui.video.response.ChannelListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class ChannelListRequest extends TvServiceRequest {
	
	public ChannelListRequest(int channelID) {
		mPath = "/tvservice/getchannelinfo3";
		addParam("channelid", String.valueOf(channelID));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new ChannelListResponse();
		}
	}
}
