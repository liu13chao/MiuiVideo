package com.miui.video.request;

import com.miui.video.response.SpecialSubjectListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


public class SpecialSubjectListRequest extends TvServiceRequest {
	
	public SpecialSubjectListRequest(int pageNo, int pageSize, String statisticInfo) {
		mPath = "/tvservice/getspecialsubjectlist";
		addParam("pageno", String.valueOf(pageNo));
		addParam("pagesize", String.valueOf(pageSize));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new SpecialSubjectListResponse();
		}
	}
}
