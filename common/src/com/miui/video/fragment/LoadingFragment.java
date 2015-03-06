/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  LoadingFragment.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-9
 */
package com.miui.video.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.miui.video.base.BaseFragment;
import com.miui.video.util.ViewUtils;

/**
 * @author tianli
 *
 */
public class LoadingFragment extends BaseFragment {

    public static final int STATE_LOADING = 0;
    public static final int STATE_EMPTY = 1;
    public static final int STATE_RETRY = 2;
    
    public int mState = STATE_LOADING;
    
    // Views
    private FrameLayout mRoot;
    
    private View mLoadingView;
    private View mEmptyView;
    private View mRetryView;
    
//    private OnRetryListener mOnRetryListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(mRoot == null){
            initUI(inflater.getContext());
        }
        return mRoot;
    }
    
    private void initUI(Context context){
        mRoot = new FrameLayout(context);
        addView(mLoadingView);
        addView(mRetryView);
        addView(mEmptyView);
    }
    
    private void addView(View view){
        if(view != null && mRoot != null){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            view.setLayoutParams(params);
            mRoot.addView(view);
        }
    }
    
    public void setLoadingView(View view){
        if(mLoadingView != null){
            mRoot.removeView(mLoadingView);
        }
        mLoadingView = view;
        addView(mLoadingView);
    }
    
    public void setEmptyView(View view){
        if(mEmptyView != null){
            mRoot.removeView(mEmptyView);
        }
        mEmptyView = view;
        addView(mEmptyView);
    }
    
    public void setRetryView(View view){
        if(mRetryView != null){
            mRoot.removeView(mRetryView);
        }
        mRetryView = view;
        addView(mRetryView);
    }
    
    private void move2Loading(){
        mState = STATE_LOADING;
        ViewUtils.showView(mLoadingView);
        ViewUtils.hideView(mEmptyView);
        ViewUtils.hideView(mRetryView);
    }
    
    private void move2Empty(){
        mState = STATE_EMPTY;
        ViewUtils.showView(mEmptyView);
        ViewUtils.hideView(mLoadingView);
        ViewUtils.hideView(mRetryView);
    }
    
    private void move2Retry(){
        mState = STATE_RETRY;
        ViewUtils.showView(mRetryView);
        ViewUtils.hideView(mLoadingView);
        ViewUtils.hideView(mEmptyView);
    }
    
    public void moveToState(int state){
        switch(state){
        case STATE_LOADING :
            move2Loading();
            break;
        case STATE_RETRY :
            move2Retry();
            break;
        case STATE_EMPTY :
            move2Empty();
            break;
        }
    }
    
//    public void setOnRetryListener(OnRetryListener onRetryListener) {
//        this.mOnRetryListener = onRetryListener;
//    }
//
//    public static interface OnRetryListener{
//        public void onRetryAction();
//    }

}
