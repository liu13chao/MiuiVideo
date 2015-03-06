package com.miui.video.request;

import com.miui.video.response.BootResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class UploadIMEBootInfoRequest extends TvServiceRequest {

	public UploadIMEBootInfoRequest(String imei, String uniqueIdentifier, String deviceName, String networkType, 
			int deviceType, int appVersion) {
		mPath = "/tvservice/setimeibootlog";
		addParam("imei", imei);
		addParam("uniqueindentify", uniqueIdentifier);
		addParam("devicename", deviceName);
		addParam("networktype", networkType);
		addParam("devicetype", String.valueOf(deviceType));
		addParam("version", String.valueOf(appVersion));
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new BootResponse();
		}
	}
}
