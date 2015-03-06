package com.miui.video.request;

import com.miui.video.response.SetMyFavoriteResponse;
import com.miui.video.type.FavoriteItem;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class SetMyFavoriteRequest extends BaseUserRequest {
	
	public SetMyFavoriteRequest(FavoriteItem favItem, String statisticInfo) {
		mPath = "/tvservice/setbookmark";
		if(favItem != null && favItem.mediaInfo != null) {
			addParam("mediaid", String.valueOf(favItem.mediaInfo.mediaid));
			addParam("createtime", String.valueOf(favItem.utime));
			if(statisticInfo != null){
				addParam("userbehavdata", statisticInfo);
			}
		}
	}
	
	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	class Parser extends JsonParser{
		@Override
		public ServiceResponse createResponse() {
			return new SetMyFavoriteResponse();
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
