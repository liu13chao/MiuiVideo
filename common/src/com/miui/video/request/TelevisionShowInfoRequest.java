package com.miui.video.request;

import com.miui.video.response.TelevisionShowInfoResponse;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class TelevisionShowInfoRequest extends TvServiceRequest {
	
	public TelevisionShowInfoRequest(MediaInfoQuery query, String statisticInfo) {
		mPath = "/tvservice/gettvprogram";
		StringBuilder ids = new StringBuilder();
		for(int i = 0; i < query.ids.length; i++) {
			ids.append(query.ids[i]);
			if(i < query.ids.length - 1) {
				ids.append(",");
			}
		}
		addParam("channelids", String.valueOf(ids.toString()));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new TelevisionShowInfoResponse();
		}
	}
}
