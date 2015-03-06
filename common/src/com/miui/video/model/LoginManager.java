/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *  LoginManager.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2013-10-28
 */
package com.miui.video.model;

import android.content.Context;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.model.UserManager.UserAccount;
import com.miui.video.type.TokenInfo;
import com.miui.video.util.ObjectStore;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class LoginManager extends AppSingleton {

    //	public boolean mLoggedOn = false;

    private final static String TOKEN_INFO_FILENAME = "/token_info.cache";

    private String mTokenCachePath;

    private TokenInfo mTokenInfo = new TokenInfo();

    @Override
    public void init(Context context) {
        super.init(context);
        mTokenCachePath = DKApp.getSingleton(AppEnv.class).getInternalFilesDir() + 
                TOKEN_INFO_FILENAME;
        loadToken();
    }

    public boolean needLogin(){
        if (TextUtils.isEmpty(mTokenInfo.userToken) || TextUtils.isEmpty(mTokenInfo.userKey)) {
            mTokenInfo.userToken = null;
            mTokenInfo.userKey = null;
            return true;
        }
        return false;
    }



    public boolean needSecurityKey() {
        synchronized (this) {
            String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
            String token = mTokenInfo.secretToken;
            String key = mTokenInfo.secretKey;
            if (Util.isEmpty(token) || Util.isEmpty(key) || !uid.equals(mTokenInfo.uid)) {
                mTokenInfo.secretToken = null;
                mTokenInfo.secretKey = null;
                mTokenInfo.uid = uid;
                return true;
            }
            return false;
        }
    }

    //	public boolean needAuthToken(){
    //		String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
    //		if (Util.isEmpty(mTokenInfo.secretToken) || !uid.equals(mTokenInfo.uid)) {
    //			mTokenInfo.secretToken = null;
    //			mTokenInfo.secretKey = null;
    //			mTokenInfo.uid = uid;
    //			return true;
    //		}
    //		return false;
    //	}

    public synchronized void resetSecurityKey() {
        //		String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
        mTokenInfo.secretToken = null;
        mTokenInfo.secretKey = null;
        //		mTokenInfo.uid = uid;
    }

    //	public synchronized void clearAuthToken() {
    //		if( mTokenInfo != null) {
    //			String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
    //			mTokenInfo.secretToken = null;
    //			mTokenInfo.secretKey = null;
    //			mTokenInfo.uid = uid;
    //			mTokenInfo.userAccount = null;
    //		}
    //	}

    public synchronized void setSecurityKey(String key, String token) {
        if( mTokenInfo != null) {
            mTokenInfo.secretKey = key;
            mTokenInfo.secretToken = token;
            saveToken(mTokenInfo);
        }
    }

    public synchronized void setUserKey(String key, String token) {
        if( mTokenInfo != null) {
            mTokenInfo.userKey = key;
            mTokenInfo.userToken = token;
            saveToken(mTokenInfo);
        }
    }

    public synchronized void setUserAccount(UserAccount userAccount) {
        if( userAccount != null && mTokenInfo != null) {
            if(mTokenInfo.userAccount == null || 
                    !mTokenInfo.userAccount.accountName.equals(userAccount.accountName)){
                invalidate();
            }
            mTokenInfo.userAccount = userAccount;
            mTokenInfo.uid = DKApp.getSingleton(DeviceInfo.class).getUID();
            saveToken(mTokenInfo);
        }
    }

    public synchronized UserAccount getUserAccount() {
        if(mTokenInfo != null) {
            return mTokenInfo.userAccount;
        }
        return null;
    }

    public void invalidate(){
        //		mLoggedOn = false;
        mTokenInfo.userKey = null;
        mTokenInfo.userToken = null;
        mTokenInfo.secretKey = null;
        mTokenInfo.secretToken = null;
    }

    public void invalidateUserToken(){
        //		mLoggedOn = false;
        mTokenInfo.userKey = null;
        mTokenInfo.userToken = null;
    }

    public void invalidateSecurityToken(){
        //		mLoggedOn = false;
        mTokenInfo.secretKey = null;
        mTokenInfo.secretToken = null;
    }


    private void loadToken(){
        synchronized (this) {
            Object object = ObjectStore.readObject(mTokenCachePath);
            if (object != null && object instanceof TokenInfo) {
                mTokenInfo = (TokenInfo) object;
            }
        }
    }

    public synchronized TokenInfo getToken() {
        return mTokenInfo;
    }

    //	public void setToken(TokenInfo token) {
    //		if (token != null) {
    //			mTokenInfo = token;
    //		}
    //	}

    public void saveToken(TokenInfo token) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (LoginManager.this) {
                    //					if (token != null) {
                    //						authToken = token;
                    //					}
                    //					if (authToken != null) {
                    ObjectStore.writeObject(mTokenCachePath, mTokenInfo);
                    //					}
                }
            }
        }).start();
    }

    public void clearAuthToken(){
        synchronized (this) {
            if(mTokenInfo != null){
                mTokenInfo.userToken = null;
                mTokenInfo.userKey = null;
                mTokenInfo.secretKey = null;
                mTokenInfo.secretToken = null;
                saveToken(mTokenInfo);
            }
        }
    }
}
