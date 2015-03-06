/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-9
 */

package com.miui.videoplayer.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * @author tianli
 *
 */
public class BaseFragment extends Fragment{

    protected int mDismissTimeout = 5000;

    protected Activity mActivity;
    protected View mAnchor;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OnShowHideListener<BaseFragment> mOnShowHideListener;

    final public void attachActivity(Activity activity, View anchor){
        mActivity = activity;
        mAnchor = anchor;
    }

    public void hide(){
        doHide();
        clearAutoDismissTrigger();
    }

    public void show(){
        doShow();
    }

    final private void doHide(){
        Activity activity = getActivity();
        if(activity != null){
            activity.getFragmentManager().executePendingTransactions();
            if(isAdded()){
                activity.getFragmentManager().beginTransaction().
                remove(this).commitAllowingStateLoss();
                onHide();
                if(mOnShowHideListener != null){
                    mOnShowHideListener.onHide(this);
                }
            }
        }
    }

    final private void doShow(){
        try {
            if(mActivity != null && mAnchor != null){
                mActivity.getFragmentManager().executePendingTransactions();
                if(!isAdded()){
                    mActivity.getFragmentManager().beginTransaction().
                    add(mAnchor.getId(), this).commitAllowingStateLoss();
                    onShow();
                    if(mOnShowHideListener != null){
                        mOnShowHideListener.onShow(this);
                    }
                }
            }			
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final public boolean isShowing(){
        return isAdded();
    }

    public void triggerAutoDismiss(){
        mHandler.removeCallbacks(mDismissRunner);
        //		mHandler.postDelayed(mDismissRunner, mDismissTimeout);
    }

    public void clearAutoDismissTrigger(){
        mHandler.removeCallbacks(mDismissRunner);
    }


    public OnShowHideListener<BaseFragment> getOnShowHideListener() {
        return mOnShowHideListener;
    }

    public void setOnShowHideListener(
            OnShowHideListener<BaseFragment>  onShowHideListener) {
        this.mOnShowHideListener = onShowHideListener;
    }



    private Runnable mDismissRunner = new Runnable() {
        @Override
        public void run() {
            autoDismissHide();
        }
    };

    //	private Runnable mHideRunner = new Runnable() {
    //		@Override
    //		public void run() {
    //			if(isAdded()){
    //				doHide();
    //			}else{
    //				mRetryCount++; 
    //				if(mRetryCount < 10){
    //					mHandler.post(mHideRunner);	
    //				}else{
    //					mRetryCount = 0;
    //				}
    //			}
    //		}
    //	};
    //	
    //	private Runnable mShowRunner = new Runnable() {
    //		@Override
    //		public void run() {
    //			if(!isAdded()){
    //				doShow();
    //			}else{
    //				mRetryCount++; 
    //				if(mRetryCount < 10){
    //					mHandler.post(mShowRunner);
    //				}else{
    //					mRetryCount = 0;
    //				}
    //			}
    //		}
    //	};

    protected void onShow(){
    }

    protected void onHide(){
    }

    protected void autoDismissHide(){
        hide();
    }
}
