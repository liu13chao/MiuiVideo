package com.miui.video.widget.actionmode;

import java.util.Locale;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class ActionDeleteView extends RelativeLayout {
	
	private Context mContext;
	private Callback mCallback;

	private boolean mIsAnimationPlaying;
	private boolean mIsInEditMode;
    private boolean mSelectAll;

	
	private TextView mSelectView;
    private TextView mDelView;
    private View mBottomView;
    
   // Dimension
    private int mBottomHeight;
    
	private static final int ANIMATE_DURATION = 300;
	
	
	public ActionDeleteView(Context context) {
		super(context);
		init();
	}
	
	public ActionDeleteView(Context context, Callback callback) {
		super(context);
		this.mCallback = callback;
		init();
	}
	
    //init
    private void init() {
        mContext = getContext();
        initDimen();
        initView();
        initTranslation();
        setSelectCount(0);
    }
    
    private void initDimen() {
        mBottomHeight = getResources().getDimensionPixelOffset(R.dimen.delete_bottom_view_height);
    }
    
    private void initView() {
        mBottomView = LayoutInflater.from(mContext).inflate(R.layout.delete_bottom_view, this, false);
        LayoutParams bottomViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomHeight);
        bottomViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mBottomView.setLayoutParams(bottomViewParams);
        addView(mBottomView);
        mSelectView = (TextView)findViewById(R.id.select_all);
        mSelectView.setOnClickListener(mOnClickListener);
        mDelView = (TextView)findViewById(R.id.delete);
        mDelView.setOnClickListener(mOnClickListener);
    }

    private void initTranslation() {
        mBottomView.setTranslationY(mBottomHeight);
    }
	
	public boolean isInEditMode() {
		return mIsInEditMode;
	}
	
	public void startActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		if(!mIsInEditMode) {
		    mIsInEditMode = true;
			animationIn();
		}
	}
	
	public void exitActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		if(mIsInEditMode) {
		    mIsInEditMode = false;
			mSelectAll = false;
			animationOut();
		}
	}
	
	public void setSelectCount(int count){
	    if(count > 0){
	        mDelView.setText(String.format(Locale.getDefault(), getResources().getString
	                (R.string.delete_action_text), count));
	           mDelView.setEnabled(true);
	    }else{
	        mDelView.setText(R.string.delete);  
	        mDelView.setEnabled(false);
	    }
	}
	
	public void setUISelectAll() {
		mSelectAll = false;
		mSelectView.setText(R.string.select_all);
	}
	
	public void setUIUnSelectAll(){
	    mSelectAll = true;
	    mSelectView.setText(R.string.select_none);
	}
	
	//packaged method
	private void animationIn() {
		 animationInBottomView(mBottomView);
	}
	
	private void animationOut() {
		animationOutBottomView(mBottomView);
	}
	
	private void animationInBottomView(View view) {
		if(view == null) {
			return;
		}
		Animator bottomViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
				view.getHeight(), 0);
		bottomViewAnim.setInterpolator(new DecelerateInterpolator());
		bottomViewAnim.setDuration(ANIMATE_DURATION);
		bottomViewAnim.addListener(mAnimatorListener);
		bottomViewAnim.start();
	}
	
	private void animationOutBottomView(View view) {
		if(view == null) {
			return;
		}
		Animator bottomViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
				0, view.getHeight());
		bottomViewAnim.setInterpolator(new DecelerateInterpolator());
		bottomViewAnim.setDuration(ANIMATE_DURATION);
		bottomViewAnim.addListener(mAnimatorListener);
		bottomViewAnim.start();
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		    if(v == mSelectView){
		        handleSelectAllClick();
		    }else if(v == mDelView){
		        handleDeleteClick();
		    }
		}
	};
	
	private void handleSelectAllClick(){
        mSelectAll = !mSelectAll;
        if(mSelectAll) {
            setUIUnSelectAll();
            if(mCallback != null) {
                mCallback.onActionSelectAll();
            }
        } else {
            setUISelectAll();
            if(mCallback != null) {
                mCallback.onActionUnSelectAll();
            }
        }
	}
	
	private void handleDeleteClick(){
	    if(mCallback != null) {
	        mCallback.onActionDeleteClick();
	    }
	}
	
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			mIsAnimationPlaying = true;
		}
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		@Override
		public void onAnimationEnd(Animator animation) {
		    mIsAnimationPlaying = false;
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
		    mIsAnimationPlaying = false;
		}
	};
	
	public static interface Callback {
		public void onActionDeleteClick();
		public void onActionSelectAll();
	    public void onActionUnSelectAll();
	}
}
