/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *   SecurityKeyResponse.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-10-13
 */

package com.miui.video.response;

import android.text.TextUtils;

/**
 * @author tianli
 *
 */
public class SecurityTokenResponse extends TvServiceResponse {
	private String mToken;
	private String mKey;

	public String getToken() {
		return mToken;
	}
	public void setToken(String token) {
		this.mToken = token;
	}
	public String getKey() {
		return mKey;
	}
	public void setKey(String key) {
		this.mKey = key;
	}
    @Override
    public boolean isSuccessful() {
        return super.isSuccessful() && !TextUtils.isEmpty(mToken) && !TextUtils.isEmpty(mKey);
    }
}
