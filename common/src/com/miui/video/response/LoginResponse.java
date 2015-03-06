/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *   LoginResponse.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-10-14
 */

package com.miui.video.response;

import android.text.TextUtils;

/**
 * @author tianli
 *
 */
public class LoginResponse extends TvServiceResponse {
	public int level;
	public String stoken;
	public String skey;
	
    @Override
    public boolean isSuccessful() {
        return super.isSuccessful() && !TextUtils.isEmpty(stoken) && !TextUtils.isEmpty(skey);
    }
	
}
