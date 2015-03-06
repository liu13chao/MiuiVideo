/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  AnimatorFactory.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-27
 */
package com.miui.video.controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * @author tianli
 *
 */
public class AnimatorFactory {

    private static int getHeight(View view){
        int height = view.getHeight();
        if(height == 0){
            height = view.getContext().getResources().getDisplayMetrics().heightPixels;
        }
        return height;
    }
    
    private static int getWidth(View view){
        int width = view.getWidth();
        if(width == 0){
            width = view.getContext().getResources().getDisplayMetrics().widthPixels;
        }
        return width;
    }
    
    public static Animator animateInBottomView(final View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 
                getHeight(view), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addUpdateListener(new AnimatorInvalidateUpdateListener(view));
        animator.start();
        view.setVisibility(View.VISIBLE);
        return animator;
    }
    
    public static  Animator animateInTopView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 
                -getHeight(view), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addUpdateListener(new AnimatorInvalidateUpdateListener(view));
        animator.start();
        view.setVisibility(View.VISIBLE);
        return animator;
    }
    
    public static Animator animateInLeftView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 
                -getWidth(view), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addUpdateListener(new AnimatorInvalidateUpdateListener(view));
        animator.start();
        view.setVisibility(View.VISIBLE);
        return animator;
    }
    
    public static Animator animateInRightView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 
                getWidth(view), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addUpdateListener(new AnimatorInvalidateUpdateListener(view));
        view.setVisibility(View.VISIBLE);
        animator.start();
        return animator;
    }
    
    public static Animator animateOutBottomView(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationY", 
                0, view.getHeight());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addListener(new ViewGoneAnimatorListener(view));
        animator.start();
        return animator;
    }
    
    public static Animator animateOutTopView(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationY", 
                 0, -view.getHeight());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addListener(new ViewGoneAnimatorListener(view));
        animator.start();
        return animator;
    }
    
    public static Animator animateOutLeftView(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationX", 
                 0, -getWidth(view));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addListener(new ViewGoneAnimatorListener(view));
        animator.start();
        return animator;
    }
    
    public static Animator animateOutRightView(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationX", 
                0, getWidth(view));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addListener(new ViewGoneAnimatorListener(view));
        animator.start();
        return animator;
    }
    
    public static Animator animateAlphaIn(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", 
                0, 1f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.start();
        view.setVisibility(View.VISIBLE);
        return animator;
    }
    
    public static Animator animateAlphaOut(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", 
                1f, 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(UIConfig.ANIMATE_DURATION);
        animator.addListener(new ViewGoneAnimatorListener(view));
        animator.start();
        return animator;
    }
    
    public static class AnimatorInvalidateUpdateListener implements AnimatorUpdateListener{

        private View mView;
        public AnimatorInvalidateUpdateListener(View view){
            mView = view;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animator) {
            if(mView != null){
                mView.invalidate();
            }
        }
    };
}
