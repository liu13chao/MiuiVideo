package com.miui.video.request;

import com.miui.video.response.InformationListResponse;
import com.miui.video.type.MediaInfoQuery;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class InformationListRequest extends TvServiceRequest {
	
	public InformationListRequest(MediaInfoQuery query) {
		mPath = "/tvservice/getnewslistinfo";
		addParam("channelid", String.valueOf(query.ids[0]));
		addParam("pageno", String.valueOf(query.pageNo));
		addParam("pagesize", String.valueOf(query.pageSize));
		addParam("orderby", String.valueOf(query.orderBy));
		addParam("userbehavdata", String.valueOf(query.statisticInfo));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new InformationListResponse();
		}
	}
}
