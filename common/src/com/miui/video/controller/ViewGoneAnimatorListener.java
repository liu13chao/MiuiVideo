/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  ViewGoneAnimatorListener.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-27
 */
package com.miui.video.controller;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.View;

/**
 * @author tianli
 *
 */
public class ViewGoneAnimatorListener implements AnimatorListener {

    private View mView;
    
    public ViewGoneAnimatorListener(View view){
        mView = view;
    }
    
    @Override
    public void onAnimationCancel(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        if(mView != null){
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

}
