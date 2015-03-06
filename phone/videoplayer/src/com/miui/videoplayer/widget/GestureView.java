/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GestureView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.AnimatorFactory;
import com.miui.video.controller.Scheduler;
import com.miui.video.controller.UIConfig;

/**
 * @author tianli
 *
 */
public abstract class GestureView extends FrameLayout {

    protected ImageView mIcon;
    protected TextView mText;
    
    private Animator mAnimator = null;
    
    private boolean mIsShowing;
    
    public GestureView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        final Context context = getContext();
        mIcon = new ImageView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT);
        params.topMargin = getIconMarginTop();;
        params.gravity = Gravity.CENTER;
        mIcon.setLayoutParams(params);
        mIcon.setImageResource(getIcon());
        addView(mIcon);
        
        mText = new TextView(context);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.topMargin = getTextMarginTop();
        mText.setLayoutParams(params);
        mText.setTextColor(Color.WHITE);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 
                getResources().getDimensionPixelSize(R.dimen.vp_text_size_57));
        addView(mText);
        
        setVisibility(View.GONE);
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    public void triggerAutoDismiss(){
        Scheduler.getUIHandler().removeCallbacks(mDissmissRunnable);
        Scheduler.getUIHandler().postDelayed(mDissmissRunnable, 
                UIConfig.AUTO_DISMISS_TIMER);
    }
    
   final public void show(){
       if(!mIsShowing){
           animateIn();
           mIsShowing = true;
       }
       triggerAutoDismiss();
    }
   
   final public void hide(){
       if(mIsShowing){
           animateOut();
           mIsShowing = false;
       }
   }
   
   final public void gone(){
       if(mIsShowing){
           setVisibility(View.GONE);
           mIsShowing = false;
       }
   }
    
    public void animateIn(){
        if(mAnimator != null){
            mAnimator.end();
        }
        mAnimator = AnimatorFactory.animateAlphaIn(this);
    }
    
    public void animateOut(){
        if(mAnimator != null){
            mAnimator.end();
        }
        mAnimator = AnimatorFactory.animateAlphaOut(this);
    }
    
    protected abstract int getIcon();
    
    protected abstract int getIconMarginTop();
    
    protected abstract int getTextMarginTop();

    private Runnable mDissmissRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

}
