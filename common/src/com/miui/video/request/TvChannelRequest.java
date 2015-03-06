package com.miui.video.request;

import com.miui.video.response.TelevisionRecommendResponse;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class TvChannelRequest extends TvServiceRequest {
	
	public TvChannelRequest(MediaInfoQuery query) {
		mPath = "/tvservice/gettvchannelrecommendmedia";
		addParam("channelid", String.valueOf(-1));
		addParam("pageno", String.valueOf(query.pageNo));
		addParam("pagesize", String.valueOf(query.pageSize));
		addParam("orderby", String.valueOf(query.orderBy));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new TelevisionRecommendResponse();
		}
	}
}
