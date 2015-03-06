package com.miui.video.request;

import com.miui.video.response.InfoRecommendResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class InfoRecommendRequest extends TvServiceRequest {

	public InfoRecommendRequest(int mediaid, int channelID, String statisticInfo) {
		mPath = "/tvservice/getnewsmedia";
		addParam("mediaid", String.valueOf(mediaid));
		addParam("channelid", String.valueOf(channelID));
		addParam("pageno", "1");
		addParam("pagesize", "10");
		addParam("orderby", "-1");
		addParam("userbehavdata", statisticInfo);

	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new InfoRecommendResponse();
		}
	}
	
}
