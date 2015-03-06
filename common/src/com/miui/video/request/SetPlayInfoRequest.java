package com.miui.video.request;

public class SetPlayInfoRequest extends TvServiceRequest {
	
	public SetPlayInfoRequest(String statisticInfo) {
		mPath = "/tvservice/setplayinfo";
		addParam("playinfo", statisticInfo);
	}
}
