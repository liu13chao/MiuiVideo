package com.miui.video.request;

import com.miui.video.response.AddonListResponse;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class AddonListRequest extends TvServiceRequest {
	
	public AddonListRequest(int pageNo, int pageSize, String statisticInfo) {
		mPath = "/tvservice/getpluginlist";
		addParam("pageno", String.valueOf(pageNo));
		addParam("pagesize", String.valueOf(pageSize));
		if (!Util.isEmpty(statisticInfo)) {
			addParam("userbehavdata", statisticInfo);
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new AddonListResponse();
		}
	}
	
}
