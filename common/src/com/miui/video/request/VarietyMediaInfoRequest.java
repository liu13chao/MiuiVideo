package com.miui.video.request;

import com.miui.video.response.VarietyListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


public class VarietyMediaInfoRequest extends TvServiceRequest{
	
	public VarietyMediaInfoRequest(int mediaID, int year,
			int pageNo, int pageSize, int orderBy) {
		mPath = "/tvservice/getvideoinfor";
		addParam("mediaid", String.valueOf(mediaID));
		addParam("year", String.valueOf(year));
		addParam("pageno", String.valueOf(pageNo));
		addParam("pagesize", String.valueOf(pageSize));
		addParam("orderby", String.valueOf(orderBy));
		addParam("posterptf", String.valueOf(0));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new VarietyListResponse();
		}
	}
}
