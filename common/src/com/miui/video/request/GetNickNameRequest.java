package com.miui.video.request;

import java.util.List;
import com.miui.video.response.GetNickNameResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class GetNickNameRequest extends ServiceRequest {

	private static final String NICKNAME_URL_ROOT = "https://api.account.xiaomi.com/pass/usersCard?ids=";

	public GetNickNameRequest(List<String> userIds) {
		if(userIds == null) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < userIds.size(); i++) {
			sb.append(userIds.get(i));
			if(i != userIds.size() - 1) {
				sb.append(",");
			}
		}
		mUrl = NICKNAME_URL_ROOT + sb.toString();
	}

	@Override
	protected JsonParser createParser() {
		return new Parser();
	}

	class Parser extends JsonParser {
		@Override
		public ServiceResponse createResponse() {
			return new GetNickNameResponse();
		}
	}
}
