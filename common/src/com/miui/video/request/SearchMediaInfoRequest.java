package com.miui.video.request;

import com.miui.video.response.SearchMediaInfoResponse;
import com.miui.video.type.SearchInfo;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class SearchMediaInfoRequest extends TvServiceRequest {

	public SearchMediaInfoRequest(SearchInfo searchInfo) {
		mPath = "/tvservice/searchmedia";
		addParam("medianame", searchInfo.mediaName);
		addParam("searchtype", String.valueOf(searchInfo.mediaNameSearchType));
		addParam("searchmask", String.valueOf(searchInfo.searchMask));
		addParam("pageno", String.valueOf(searchInfo.pageNo));
		addParam("pagesize", String.valueOf(searchInfo.pageSize));
		addParam("userbehavdata", searchInfo.statisticInfo);
		addParam("posterptf", String.valueOf(0));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new SearchMediaInfoResponse();
		}
	}
}
