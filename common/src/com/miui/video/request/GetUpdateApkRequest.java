package com.miui.video.request;

import com.miui.video.response.GetUpdateApkResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;


public class GetUpdateApkRequest extends TvServiceRequest {

	public GetUpdateApkRequest(String miuiversion, String curVersionCode) {
		mPath = "/tvservice/getmobileupgradeinfo";
		addParam("miuiversion", miuiversion);
		addParam("apkversion", curVersionCode);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new GetUpdateApkResponse();
		}
	}
}
