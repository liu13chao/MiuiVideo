/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   TokenInfo.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-9-6 
 */
package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.model.UserManager.UserAccount;

/**
 * @author tianli
 *
 */
public class TokenInfo implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	public String uid;   //  unique identifier for device
	
	public UserAccount userAccount;  // token info for XiaoMi account.
	
	public String secretKey;    // secret key for login
	public String secretToken;  // secret token for login

	public String userToken;
	public String userKey;
}
