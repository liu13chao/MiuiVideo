package com.miui.video.request;

import com.miui.video.response.GetMyFavoriteResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class GetMyFavoriteRequest extends BaseUserRequest {
	
	public GetMyFavoriteRequest() {
		mPath = "/tvservice/getbookmark";
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new GetMyFavoriteResponse();
		}
	}

	@Override
	public boolean needUserId() {
		return true;
	}
}
