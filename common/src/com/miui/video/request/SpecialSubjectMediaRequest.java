package com.miui.video.request;

import com.miui.video.response.SpecialSubjectMediaResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class SpecialSubjectMediaRequest extends TvServiceRequest {
	
	public SpecialSubjectMediaRequest(int id, String statisticInfo) {
		mPath = "/tvservice/getspecialsubjectmedia";
		addParam("channelid", String.valueOf(id));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new SpecialSubjectMediaResponse();
		}
	}
}
