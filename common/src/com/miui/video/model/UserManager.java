/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   UserManager.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-9-10
 */

package com.miui.video.model;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.type.TokenInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 *@author xuanmingliu
 *
 */

public class UserManager extends AppSingleton{
	
	public static final String TAG = UserManager.class.getName();
	
	public static final String ACCOUNT_TYPE_XIAOMI = "com.xiaomi";
	public static final String AUTH_TOKEY_TYPE_XIAOMI = "video";
//	public static final String ACCOUNT_TYPE_XIAOMI = ExtraIntent.XIAOMI_ACCOUNT_TYPE;
//	public static final String AUTH_TOKEY_TYPE_XIAOMI = "video";

	//认证信息
//	private UserAccount mAccountInfo;
	//认证是否成功
	private boolean mAuthSuccess;
	//是否正在认证中
	private boolean mAuthenticating;
	//认证失败原因
	private int    mAuthenticatedFailedCode;
	// 认证Intent
	// private Intent authIntent;
//	private OnAuthenticateListener  mOnAuthListener;
	
	private Account   mXiaomiAccount;
	
	// private boolean needAuthFailedLogin;
	private AccountManager mAccountManager;
	private LoginManager mLoginManager;
	
	private AsyncAccountAuthTokenTask mAuthTokenTask;
	
	//认证成功后的账户信息
	public static class UserAccount implements Serializable {
		private static final long serialVersionUID = 2L;

		public String accountName;
		public String authToken;
		public String ssec;
	}
	
	public static interface OnAuthenticateListener {
		public void onAuthenticatedResult(boolean authSuccess,
				UserAccount accountInfo);
	}
		
	private static final int XIAOMI_ACCOUNT_TYPE_UNSUPPORT_CODE = 0x80000000;
	private static final int NO_LOGIN_XIAOMI_ACCOUNT_CODE =  0x80000001;
	private static final int AUTHENTICATING_CODE = 0x80000002;
	private static final int AUTHENTICATED_FAILED_CODE = 0x80000003;
	private static final int AUTHENTICATING_MANUALLY_CODE = 0x80000004;
	
	@Override
    public void init(Context context) {
        super.init(context);
        mAccountManager = AccountManager.get(context);
        mLoginManager = DKApp.getSingleton(LoginManager.class);
    }

    public boolean needAuthenticate(String accountType) {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if (tokenInfo != null) {
			String uid = DKApp.getSingleton(DeviceInfo.class).getUID();
			if (uid.equals(tokenInfo.uid) && tokenInfo.userAccount != null) {
				Account account = getAccount(accountType);
				if (account != null && !Util.isEmpty(account.name)) {
					if (tokenInfo.userAccount != null && account.name
									.equals(tokenInfo.userAccount.accountName)
							&& !Util.isEmpty(tokenInfo.userAccount.authToken)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean isAccountChanged(){
		TokenInfo tokenInfo = mLoginManager.getToken();
		Account account = getAccount(ACCOUNT_TYPE_XIAOMI);
		if(account == null || TextUtils.isEmpty(account.name)){
			if(tokenInfo.userAccount != null && 
					!TextUtils.isEmpty(tokenInfo.userAccount.accountName)){
				return true;
			}
		}else if(account != null && !TextUtils.isEmpty(account.name)){
			if(tokenInfo.userAccount == null || !account.name.equals(
					tokenInfo.userAccount.accountName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean needAuthenticate() {
		return needAuthenticate(UserManager.ACCOUNT_TYPE_XIAOMI);
	}

	public void authenticateAccount(Activity context, OnAuthenticateListener onAuthListener) {
		if( mAuthTokenTask != null)
			return;
		
		resetUserAccountInfo();
//		this.mOnAuthListener = onAuthListener;
		if (isAuthenticating()) {
            mAuthenticatedFailedCode = AUTHENTICATING_CODE;
            if (onAuthListener != null) {
                onAuthListener.onAuthenticatedResult(mAuthSuccess, null);
            }
            return;
        }
		if (!isExistAuthenticator(ACCOUNT_TYPE_XIAOMI)) {
			//当前手机不存在小米账户类型
			mAuthenticatedFailedCode = XIAOMI_ACCOUNT_TYPE_UNSUPPORT_CODE;
			if (onAuthListener != null) {
				onAuthListener.onAuthenticatedResult(mAuthSuccess, null);
			}
			return;
		}
		mXiaomiAccount = getAccount(ACCOUNT_TYPE_XIAOMI);
		if (mXiaomiAccount == null) {
		    // 当前手机上没有小米账号登陆
            mAuthenticatedFailedCode = NO_LOGIN_XIAOMI_ACCOUNT_CODE;
            if (onAuthListener != null) {
                onAuthListener.onAuthenticatedResult(mAuthSuccess, null);
            }
			return;
		}
		
		mAuthTokenTask = new AsyncAccountAuthTokenTask(context, mXiaomiAccount, onAuthListener);
		mAuthTokenTask.execute();
	}
	
	public void invalidateAuthToken() {
		TokenInfo tokenInfo = mLoginManager.getToken();
		if (tokenInfo.userAccount != null) {
			mAccountManager.invalidateAuthToken(ACCOUNT_TYPE_XIAOMI,
					tokenInfo.userAccount.authToken + ","
							+ tokenInfo.userAccount.ssec);
			tokenInfo.userAccount = null;
			mLoginManager.saveToken(tokenInfo);
		}
	}

	public Account getAccount(String accountType) {
		Account[] existAccounts = mAccountManager.getAccountsByType(accountType);
		if (existAccounts != null && existAccounts.length > 0) {
			return existAccounts[0];
		}
		return null;
	}

	private boolean isExistAuthenticator(String accountType) {
		AuthenticatorDescription[] authDescs = mAccountManager
				.getAuthenticatorTypes();
		for (AuthenticatorDescription authDesc : authDescs) {
			if (authDesc.type.equals(accountType))
        		return true;
        }		
        return false;
	}
	
	private void resetUserAccountInfo() {
		mAuthSuccess = false;
		mAuthenticating = false;
		mAuthenticatedFailedCode = 0;
//		mAccountInfo.accountName = null;
//		mAccountInfo.authToken = null;
//		mAccountInfo.ssec = null;
//		mOnAuthListener = null;
		mXiaomiAccount = null;
	}
	
	public boolean isAuthenticating() {
		return mAuthenticating;
	}
	
	public boolean isNoAccount(){
	    return getAccount(ACCOUNT_TYPE_XIAOMI) == null;
	}
	
	public boolean isAuthSuccess() {
		return mAuthSuccess;
	}
	
	public String getAuthFailedReason() {
		Resources resMgr = DKApp.getAppContext().getResources();
		switch (mAuthenticatedFailedCode) {
		case XIAOMI_ACCOUNT_TYPE_UNSUPPORT_CODE:
			return resMgr.getString(R.string.xiaomi_account_type_unsupported);
		case NO_LOGIN_XIAOMI_ACCOUNT_CODE:
			return resMgr.getString(R.string.no_login_xiaomi_account);
		case AUTHENTICATING_CODE:
			return resMgr.getString(R.string.authenticating);
		case AUTHENTICATED_FAILED_CODE:
			return resMgr.getString(R.string.authenticated_failed);
		case AUTHENTICATING_MANUALLY_CODE:
			return resMgr.getString(R.string.authenticating_manually);
		}		
		return resMgr.getString(R.string.authenticated_failed);
	}
	
	public int getAuthFailedCode() {
		return mAuthenticatedFailedCode;
	}
	
	public void addAccount(Activity context, AccountManagerCallback<Bundle> callback){
	    mAccountManager.addAccount(ACCOUNT_TYPE_XIAOMI, AUTH_TOKEY_TYPE_XIAOMI, 
                null, null, context, callback, null);
	}
		
	private class AsyncAccountAuthTokenTask extends AsyncTask<Void, Void, Void> {
		private boolean mTaskAuthSuccess;
		private WeakReference<Activity> mContextRef;
		private OnAuthenticateListener mAuthListener;
		private Account mAccount;
		private Intent mKeyIntent;
		private String mAccountName;
		private String mAuthToken;
		private String mSsec;

		public AsyncAccountAuthTokenTask(Activity context, Account account, OnAuthenticateListener listener) {
			this.mContextRef = new WeakReference<Activity>(context);
			this.mAccount = account;
			this.mTaskAuthSuccess = false;
			mAuthListener = listener;
    	}
    	  	
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mAuthSuccess = false;
			mAuthenticating = true;
			mAuthenticatedFailedCode = AUTHENTICATING_CODE;
			// authenticateIntent = null;
		}
                                                                                                                                                                                                      
		@Override
		protected Void doInBackground(Void... params) {
			mTaskAuthSuccess = false;
			try {
				AccountManagerFuture<Bundle> accountFuture = mAccountManager
						.getAuthToken(mAccount, AUTH_TOKEY_TYPE_XIAOMI, null,
								mContextRef.get(), null, null);
				Bundle returnData = null;
				DKLog.i(TAG, "getResult called");
				returnData = accountFuture.getResult();
				if( returnData.containsKey(AccountManager.KEY_ACCOUNT_NAME) 
						&& returnData.containsKey(AccountManager.KEY_AUTHTOKEN)) {
					mAccountName = returnData.getString(AccountManager.KEY_ACCOUNT_NAME);
					String tokenString = returnData.getString(AccountManager.KEY_AUTHTOKEN);
					String []splitResults = tokenString.split(",");
					mAuthToken = splitResults[0];
					mSsec = splitResults[1];
					mTaskAuthSuccess = true;
				} else if (returnData.containsKey(AccountManager.KEY_INTENT)) {
					mKeyIntent = returnData.getParcelable(AccountManager.KEY_INTENT);
				} else if (returnData.containsKey("errorCode")) {
					DKLog.e(TAG, returnData.getString("errorMessage"));
				}
			} catch (OperationCanceledException e) {
				DKLog.e(TAG, "OperationCanceledException", e);
			} catch (AuthenticatorException e) {
				DKLog.e(TAG, "AuthenticatorException", e);
			} catch (IOException e) {
				DKLog.e(TAG, "IOException", e);
			} catch (Exception e) {
				DKLog.e(TAG, "Exception", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);			
			DKLog.i(TAG, "auth success : " + mTaskAuthSuccess);
			mAuthTokenTask = null;
			mAuthenticating = false;
			UserAccount accountInfo = new UserAccount();
			if (mTaskAuthSuccess) {
				mAuthSuccess = true;
				mAuthenticatedFailedCode = 0;
				accountInfo.accountName = mAccountName;
				accountInfo.authToken = mAuthToken;
				accountInfo.ssec = mSsec;
				mLoginManager.setUserAccount(accountInfo);
			}  else {
				mAuthSuccess = false;
				if (mKeyIntent != null) {
					// authIntent = keyIntent;
					mAuthenticatedFailedCode = AUTHENTICATING_MANUALLY_CODE;
				} else {
					mAuthenticatedFailedCode = AUTHENTICATED_FAILED_CODE;
				}	
			}
			// authFailedLogin = (tokenInfo == null && !authSuccess);
			if (mAuthListener != null) {
				mAuthListener.onAuthenticatedResult(mAuthSuccess, accountInfo);
			}
		}
    }
}


