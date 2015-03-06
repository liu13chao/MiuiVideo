package com.miui.video.request;

import com.miui.video.response.ReviewListResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class ReviewListRequest extends TvServiceRequest {
	
	public ReviewListRequest(int mediaID, int pageNo, int pageSize, int reviewType) {
		mPath = "/tvservice/getfilmreview";
		addParam("mediaid", String.valueOf(mediaID));
		addParam("pageno", String.valueOf(pageNo));
		addParam("pagesize", String.valueOf(pageSize));
		addParam("reviewtype", String.valueOf(reviewType));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new ReviewListResponse();
		}
	}

}
