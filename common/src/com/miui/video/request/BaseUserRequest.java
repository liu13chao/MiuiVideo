/**
 * 
 */
package com.miui.video.request;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.LoginManager;
import com.miui.video.model.UserManager;
import com.miui.video.response.TvServiceResponse;
import com.miui.video.type.TokenInfo;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

/**
 * @author tianli
 *
 */
public class BaseUserRequest extends TvServiceRequest {
	
	private LoginManager mLoginManager;
	
	public BaseUserRequest(){
		mLoginManager = DKApp.getSingleton(LoginManager.class);
	}

	@Override
	protected void onRequestInBackground() {
		int retry = 0;
		do {
			if(mLoginManager.needLogin()) {
				ServiceResponse response = DKApi.userLoginSync();
				if(response != null && response.isSuccessful()) {
					super.onRequestInBackground();
					return;
				}else{
					mResponse = createParser().createResponse();
					mResponse.setStatus(response.getStatus());
				}
			} else {
				super.onRequestInBackground();
				int result = mResponse.getStatus();
				if(TvServiceResponse.isUserTokenExpired(result)) {
					mLoginManager.invalidateUserToken();
					DKApp.getSingleton(UserManager.class).invalidateAuthToken();
				} else {
					return;
				}
			}
		}while(retry++ < 3);
	}
	
	@Override
	protected String token() {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if(tokenInfo != null){
			return tokenInfo.userToken;
		}
		return null;
	}

	@Override
	protected String key() {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if(tokenInfo != null){
			return tokenInfo.userKey;
		}
		return null;
	}

	@Override
	protected boolean isSecurity() {
		return true;
	}

}
