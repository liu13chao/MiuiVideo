package com.miui.video.request;

public class UploadComUserDataRequest extends TvServiceRequest {
	
	public UploadComUserDataRequest(String statisticInfo) {
		mPath = "/tvservice/setcommonlog";
		addParam("userbehavdata", statisticInfo);
	}
}
