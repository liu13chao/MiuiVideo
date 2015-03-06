/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseMenuPopup.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-30
 */
package com.miui.videoplayer.menu.popup;

import android.animation.Animator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.controller.AnimatorFactory;
import com.miui.video.controller.Scheduler;
import com.miui.video.controller.UIConfig;
import com.miui.videoplayer.fragment.OnShowHideListener;

/**
 * @author tianli
 *
 */
public class BaseMenuPopup extends RelativeLayout {

    private ImageView mMaskView;
    private TextView mTitleText;
    private View mTitleLayout;
    protected ListView mListView;
    protected LinearLayout mContentView;
    
    private boolean mIsShowing = false;
    
    private Animator mAnimator;
    
    private OnShowHideListener<BaseMenuPopup> mShowHideListener;
    
    public BaseMenuPopup(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        mMaskView = new ImageView(getContext());
        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mMaskView.setImageResource(R.drawable.play_mask_right_l);
        mMaskView.setScaleType(ScaleType.FIT_XY);
        mMaskView.setLayoutParams(p);
        addView(mMaskView);
        
        int width = getPopupWidth();
        
        mContentView = new LinearLayout(getContext());
        p = new LayoutParams(width, LayoutParams.MATCH_PARENT);
        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mContentView.setLayoutParams(p);
        mContentView.setGravity(Gravity.CENTER_VERTICAL);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        addView(mContentView);
        
        mTitleLayout = LayoutInflater.from(getContext()).inflate(R.layout.vp_popup_base_title, mContentView, false);
        mContentView.addView(mTitleLayout);
        mTitleText = (TextView)findViewById(R.id.vp_popup_base_title_name);
        
        mListView = new ListView(getContext());
//        p = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
//        p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        p.addRule(RelativeLayout.CENTER_VERTICAL);
//        p.addRule(RelativeLayout.BELOW, mTitleId);
//        mListView.setLayoutParams(p);
        mListView.setDividerHeight(1);
        mListView.setDivider(getResources().getDrawable(R.drawable.vp_divider_bg_30));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.vp_menu_popup_list_margin_right);
        mListView.setLayoutParams(params);

        mContentView.addView(mListView);
        
        setOnClickListener(mDismissClick);
    }
    
    protected int getPopupWidth(){
        return getContext().getResources().getDimensionPixelSize(R.dimen.vp_base_popup_width);
    }
    
    private void adjustPopupWidth(){
        ViewGroup.LayoutParams p = mContentView.getLayoutParams();
        if(p != null){
            p.width = getPopupWidth();
            mContentView.setLayoutParams(p);
        }
    }
    
    public void setTitle(CharSequence title){
        mTitleText.setText(title);
    }
    
    public void show(ViewGroup anchor){
        if(!isShowing()){
            adjustPopupWidth();
            clearAnimator();
            remove();
            anchor.addView(this, generateParams());
            animateIn();
            mIsShowing = true;
            onShowStateChanged();
            triggerAutoDismiss();
        }
    }
    
    public void dismiss(){
        if(isShowing()){
            clearAnimator();
            animateOut();
            mIsShowing = false;
            onShowStateChanged();
            clearAutoDismiss();
        }
    }
    
    private void onShowStateChanged(){
        if(mShowHideListener != null){
            if(mIsShowing){
                mShowHideListener.onShow(this);
            }else{
                mShowHideListener.onHide(this);
            }
        }
    }
    
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(changedView == this){
            if(visibility == VISIBLE){
                if(!mIsShowing){
                    mIsShowing = true;
                    onShowStateChanged();
                }
            }else{
                if(mIsShowing){
                    mIsShowing = false;
                    onShowStateChanged();
                }
                remove();
            }
        }
    }

    public boolean isShowing(){
        return mIsShowing;
    }
    
    private void remove(){
        if(getParent() instanceof ViewGroup){
            ViewGroup parent = (ViewGroup)getParent();
            parent.removeView(this);
        }
    }
    
    private ViewGroup.LayoutParams generateParams(){
        return   new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    
    private void clearAnimator(){
        if(mAnimator != null){
            mAnimator.end();
        }
    }
    
    private void animateIn(){
        mAnimator = AnimatorFactory.animateInRightView(this);
    }
    
    private void animateOut(){
        mAnimator = AnimatorFactory.animateOutRightView(this);
    }
    
    private void triggerAutoDismiss(){
        Scheduler.getUIHandler().removeCallbacks(mAutoDismissRunner);
        Scheduler.getUIHandler().postDelayed(mAutoDismissRunner, UIConfig.AUTO_DISMISS_TIMER);
    }
    
    private void clearAutoDismiss(){
        Scheduler.getUIHandler().removeCallbacks(mAutoDismissRunner);
    }
    
    public void setShowHideListener(
            OnShowHideListener<BaseMenuPopup> mShowHideListener) {
        this.mShowHideListener = mShowHideListener;
    }

    public boolean needPauseVideo(){
        return true;
    }
    
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        triggerAutoDismiss();
        return super.dispatchTouchEvent(event);
    }



    private OnClickListener mDismissClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == BaseMenuPopup.this){
                dismiss();
            }
        }
    };
    
    private Runnable mAutoDismissRunner = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };
}
