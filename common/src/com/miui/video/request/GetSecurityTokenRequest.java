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

import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.ApiConfig;
import com.miui.video.model.LoginManager;
import com.miui.video.model.UserManager.UserAccount;
import com.miui.video.response.SecurityTokenResponse;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class GetSecurityTokenRequest extends TvServiceRequest {

	UserAccount  mAccount;
	 private LoginManager mLoginManager;
	 
	public GetSecurityTokenRequest(){
		mPath = "/security/generatedevicesecurity";
		mLoginManager = DKApp.getSingleton(LoginManager.class);
		mAccount =  mLoginManager.getUserAccount();
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
		if(mAccount != null && !TextUtils.isEmpty(mAccount.authToken)){
	        return mAccount.authToken;
	    }
		return ApiConfig.API_ACTIVE_TOKEN;
	}

	@Override
	protected String key() {
		if(mAccount != null && !TextUtils.isEmpty(mAccount.ssec)){
			return mAccount.ssec;
		}
		return ApiConfig.API_ACTIVE_KEY;
	}

	static class Parser extends JsonParser{
		@Override
		public ServiceResponse createResponse() {
			return new SecurityTokenResponse();
		}
	}
	
	   @Override
	    public boolean needUserId() {
	        if(mAccount != null && !TextUtils.isEmpty(mAccount.authToken) && 
	                !TextUtils.isEmpty(mAccount.ssec)){
	            return true;
	        }
	        return false;
	    }
	
}
