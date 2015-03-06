package com.miui.video.request;

import com.miui.video.response.MediaDetailInfoResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class MediaDetailInfoRequest extends TvServiceRequest {
	
	public MediaDetailInfoRequest(int mediaID, boolean getAll, int fee, String statisticInfo) {
		mPath = "/tvservice/getmediadetail2";
		addParam("mediaid", String.valueOf(mediaID));
		addParam("fee", String.valueOf(fee));
		addParam("userbehavdata", statisticInfo);
		addParam("pageno", String.valueOf(1));
		addParam("orderby", String.valueOf(-1));
		if(getAll) {
			addParam("pagesize", String.valueOf(1000));
		} else {
			addParam("pagesize", String.valueOf(1));
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new MediaDetailInfoResponse();
		}
	}
}
