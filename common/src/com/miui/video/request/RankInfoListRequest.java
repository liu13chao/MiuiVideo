package com.miui.video.request;

import com.miui.video.response.RankInfoListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class RankInfoListRequest extends TvServiceRequest {
	
	public RankInfoListRequest(int channelID, int pageNo, int pageSize, String statisticInfo) {
		mPath = "/tvservice/getrankinglistmediainfor";
		addParam("channelid", String.valueOf(channelID));
		addParam("pageno", String.valueOf(pageNo));
		addParam("pagesize", String.valueOf(pageSize));
		addParam("postertype", String.valueOf(-1));
		addParam("listtype", String.valueOf(-1));
		addParam("orderby", String.valueOf(-1));
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new RankInfoListResponse();
		}
	}
}
