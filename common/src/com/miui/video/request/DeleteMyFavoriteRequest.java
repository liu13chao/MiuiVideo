package com.miui.video.request;

import com.miui.video.response.DeleteMyFavoriteResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class DeleteMyFavoriteRequest extends BaseUserRequest {
	
	public DeleteMyFavoriteRequest(int[] mediaIds, String statisticInfo) {
		mPath = "/tvservice/deletebookmark";
		if(mediaIds != null) {
			StringBuilder ids = new StringBuilder();
			for(int i = 0; i< mediaIds.length; i++) {
				ids.append(mediaIds[i]);
				if(i < mediaIds.length - 1) {
					ids.append(",");
				}
			}
			addParam("mediaid", ids.toString());
		}
		if(statisticInfo != null){
			addParam("userbehavdata", statisticInfo);
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		
		@Override
		public ServiceResponse createResponse() {
			return new DeleteMyFavoriteResponse();
		}
	}

	@Override
	public boolean needUserId() {
		return true;
	}
	

}
