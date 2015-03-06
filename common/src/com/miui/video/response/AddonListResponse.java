package com.miui.video.response;

import com.miui.video.type.AddonInfo;


public class AddonListResponse extends TvServiceResponse {
	public int totalcount;
	public AddonInfo[] data;
	
	@Override
	public void completeData() {
	}
}
