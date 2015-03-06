package com.miui.video.model;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;

import com.miui.video.DKApp;
import com.miui.video.model.UserManager.OnAuthenticateListener;
import com.miui.video.model.UserManager.UserAccount;

public abstract class AuthenticateAccountManager {
	
	private Activity mActivity;
	
	//manager
	private UserManager mUserManager;
	
	public AuthenticateAccountManager(Activity activity) {
		this.mActivity = activity;
		init();
	}
	
	//init
	private void init() {
		mUserManager = DKApp.getSingleton(UserManager.class);
	}
	
	//abstract method
	protected abstract void onAuthSuccess();
	protected abstract void onAuthFailed(String failedReason);
	protected abstract void onAuthNoAccount();
	
	//public method
	public void authAccount() {
		if(mUserManager.needAuthenticate()) {
			mUserManager.authenticateAccount(mActivity, mOnAuthenticateListener);
		} else {
			onAuthSuccess();
		}
	}
	
	public boolean needAuthenticate() {
		return mUserManager.needAuthenticate();
	}
	
	public boolean isNoAccount() {
		return mUserManager.isNoAccount();
	}
	
	public String getXiaoMiAccountName(){
	    Account account = mUserManager.getAccount(UserManager.ACCOUNT_TYPE_XIAOMI);
	    if(account != null){
	        return account.name;
	    }
	    return null;
	}

	//auth callback
	private OnAuthenticateListener mOnAuthenticateListener = new OnAuthenticateListener() {
		
		@Override
		public void onAuthenticatedResult(boolean authSuccess,
				UserAccount accountInfo) {
			if(authSuccess) {
				onAuthSuccess();
			} else if(mUserManager.isNoAccount()){
				onAuthNoAccount();
				mUserManager.addAccount(mActivity, new AccountManagerCallback<Bundle>() {

					@Override
					public void run(AccountManagerFuture<Bundle> arg0) {
						
					}
					
				});
			} else {
				onAuthFailed(mUserManager.getAuthFailedReason());
			}
			
		}
	};
}
