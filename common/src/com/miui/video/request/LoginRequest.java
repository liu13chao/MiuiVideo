/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *   LoginRequest.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-10-14
 */

package com.miui.video.request;

import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.LoginManager;
import com.miui.video.response.LoginResponse;
import com.miui.video.response.SecurityTokenResponse;
import com.miui.video.response.TvServiceResponse;
import com.miui.video.type.TokenInfo;
import com.xiaomi.mitv.common.security.Security;
import com.xiaomi.mitv.common.webservice.JsonParser;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class LoginRequest extends TvServiceRequest {
	
	GetSecurityTokenRequest mSecurityRequest = new GetSecurityTokenRequest(); 
	private LoginManager mLoginManager;

	public LoginRequest(){
		mPath = "/tvservice/login";
		mLoginManager = DKApp.getSingleton(LoginManager.class);
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
	protected void onRequestInBackground() {
		if(mLoginManager.needSecurityKey()){
			ServiceResponse response = DKApi.getSecurityTokenSync();
			if(response instanceof SecurityTokenResponse && response.isSuccessful()){
				SecurityTokenResponse realResponse = (SecurityTokenResponse)response;
				mLoginManager.setSecurityKey(realResponse.getKey(), realResponse.getToken());
			}else{
				mResponse = createParser().createResponse();
				mResponse.setStatus(response.getStatus());
				return;
			}
		}
		super.onRequestInBackground();
        String secretKey = key();
		if(mResponse.isSuccessful() && !TextUtils.isEmpty(secretKey)){
			LoginResponse realResponse = (LoginResponse)mResponse;
			realResponse.skey = Security.decrypt(realResponse.skey.getBytes(), secretKey.getBytes());
			realResponse.stoken = Security.decrypt(realResponse.stoken.getBytes(), secretKey.getBytes());
			mLoginManager.setUserKey(realResponse.skey, realResponse.stoken);
		}else{
			int status = mResponse.getStatus();
			if(TvServiceResponse.isUserTokenExpired(status)){
				mLoginManager.invalidateSecurityToken();
			}
		}
	}

	@Override
	protected String token() {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if(tokenInfo != null){
			return tokenInfo.secretToken;
		}
		return null;
	}

	@Override
	protected String key() {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if(tokenInfo != null){
			return tokenInfo.secretKey;
		}
		return null;
	}

	static class Parser extends JsonParser{
		@Override
		public ServiceResponse createResponse() {
			return new LoginResponse();
		}
	}

	@Override
	public boolean needUserId() {
		return true;
	}
	
	
}
