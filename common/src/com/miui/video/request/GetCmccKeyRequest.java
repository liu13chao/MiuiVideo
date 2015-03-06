/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *   GetUserTokenRequest.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-10-13
 */

package com.miui.video.request;

import com.miui.video.api.ApiConfig;
import com.miui.video.response.CmccKeyResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class GetCmccKeyRequest extends TvServiceRequest {

	public GetCmccKeyRequest(String channelid, String appid){
		mPath = "/security/videobasesecurity";
		addParam("channelid", channelid);
		addParam("appid", appid);
	}

	@Override
	protected JsonParser createParser() {
		return new Parser();
	}
	
	@Override
	protected boolean isSecurity() {
		return true;
	}

	@Override
	protected String token() {
		return ApiConfig.API_ACTIVE_TOKEN;
	}

	@Override
	protected String key() {
		return ApiConfig.API_ACTIVE_KEY;
	}

	static class Parser extends JsonParser{
		@Override
		public ServiceResponse createResponse() {
			return new CmccKeyResponse();
		}
	}
	
}
