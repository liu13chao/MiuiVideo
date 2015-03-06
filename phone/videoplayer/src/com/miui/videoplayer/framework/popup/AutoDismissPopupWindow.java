/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AutoDismissPopupWindow.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-16
 */

package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * @author tianli
 *
 */
public abstract class AutoDismissPopupWindow extends ManagedPopupWindow {

	protected int mDismissTimeout = 5000;
	
	protected Activity mActivity;
	protected View mAnchor;
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	public AutoDismissPopupWindow() {
		super();
	}

	public AutoDismissPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	public AutoDismissPopupWindow(View contentView) {
		super(contentView);
	}

	public void triggerAutoDismiss(){
		mHandler.removeCallbacks(mDismissRunner);
		mHandler.postDelayed(mDismissRunner, mDismissTimeout);
	}
	
	public void clearAutoDismissTrigger(){
		mHandler.removeCallbacks(mDismissRunner);
	}
	
	private Runnable mDismissRunner = new Runnable() {
		@Override
		public void run() {
			dismiss();
		}
	};
	
}
