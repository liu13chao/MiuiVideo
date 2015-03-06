package com.miui.video.request;

import com.miui.video.response.CommentResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class CommentRequest extends BaseUserRequest {
	
	public CommentRequest(int mediaID, int score, String comment, String statisticInfo) {
		mPath = "/tvservice/setfilmreview";
		addParam("mediaid", String.valueOf(mediaID));
		addParam("score", String.valueOf(score));
		addParam("filmreview", comment);
		addParam("userbehavdata", statisticInfo);
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		@Override
		public ServiceResponse createResponse() {
			return new CommentResponse();
		}
	}

	@Override
	public boolean needUserId() {
		return true;
	}

}
