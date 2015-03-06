/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  SyncManager.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-23
 */
package com.miui.video.local;

import android.accounts.Account;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.miui.video.DKApp;
import com.miui.video.model.AppSingleton;
import com.miui.video.model.UserManager;

/**
 * @author tianli
 *
 */
public abstract class SyncManager extends AppSingleton {

	// Sync Service
	private static HandlerThread mSyncThread;
	private static Handler mUiHandler;
	private static Handler mHandler;

	static {
		mSyncThread = new HandlerThread("SyncThread");
		mSyncThread.start();
		mHandler = new Handler(mSyncThread.getLooper());
		mUiHandler = new Handler(Looper.getMainLooper());
	}
		
	protected String getAccount(){
		UserManager accountInfo = DKApp.getSingleton(UserManager.class);
		if(!accountInfo.needAuthenticate()) {
			Account account = accountInfo.getAccount(UserManager.ACCOUNT_TYPE_XIAOMI);
			if (account != null){
				return account.name;
			}
		}
		return null;
	}
	
	protected void scheduleBackgroundTask(Runnable task){
		mHandler.post(task);
	}
	
	protected void scheduleUITask(Runnable task){
		mUiHandler.post(task);
	}
}
