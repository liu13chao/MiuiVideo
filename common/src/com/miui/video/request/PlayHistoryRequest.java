package com.miui.video.request;

import com.miui.video.response.PlayHistoryResponse;
import com.miui.video.type.UserDataParam;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class PlayHistoryRequest extends TvServiceRequest {
	
	public PlayHistoryRequest(UserDataParam  playHistoryInfoQuery) {
		mPath = "/tvservice/getplayhistory";
		if(playHistoryInfoQuery != null) {
			addParam("channelid", String.valueOf(playHistoryInfoQuery.channelID));
			addParam("pageno", String.valueOf(playHistoryInfoQuery.pageNo));
			addParam("pagesize", String.valueOf(playHistoryInfoQuery.pageSize));
			addParam("days", String.valueOf(playHistoryInfoQuery.latestDays));
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new PlayHistoryResponse();
		}
	}
	
	@Override
	public boolean needUserId() {
		return true;
	}
	
	@Override
	protected boolean isSecurity() {
		return true;
	}
}

