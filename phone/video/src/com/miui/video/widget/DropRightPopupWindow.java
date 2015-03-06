package com.miui.video.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.miui.video.util.DKLog;

/**
 *@author tangfuling
 *
 */

public class DropRightPopupWindow extends PopupWindow {
	
	private static final String TAG = DropRightPopupWindow.class.getName();
	
	private static final int ANIMATE_DURATION = 300;

    private FrameLayout mRootPanel;
    private View mContentView;
    private View mPartView;
    private int mPartViewWidth;
    private int mContentViewWidth;
    private int mCurrentXPosition;
    private boolean mIsAnimationPlaying;
    private boolean mDoAnimation;
    
    private int mCurrentMode = -1;
    private static final int MODE_PARTY = 0;
    private static final int MODE_ALL = 1;
 
    public DropRightPopupWindow(Context context, AttributeSet attr) {

	}
    
    public DropRightPopupWindow(Context context, View contentView, int partViewId) {
    	super(context);
        mContentView = contentView;
        mPartView = mContentView.findViewById(partViewId);
        mRootPanel = new RootPanel(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mRootPanel.addView(contentView, lp);
        setContentView(mRootPanel);
        setAnimationStyle(0);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));
    }
    
    public int getTouchResponseMinX() {
    	updateViewWidth();
    	if(mCurrentMode == MODE_PARTY) {
    		return mPartViewWidth;
    	} else if(mCurrentMode == MODE_ALL) {
    		return mContentViewWidth;
    	}
    	return 0;
    }
    
    public boolean isAnimationPlaying() {
    	return mIsAnimationPlaying;
    }
    
    public void showPartView(View parent, boolean doAnimation) {
    	mDoAnimation = doAnimation;
    	mCurrentMode = MODE_PARTY;
    	updateViewWidth();
    	if(doAnimation) {
    		playAnimation(getInAnimatorPart());
    	} else {
    		mContentView.setTranslationX(mPartViewWidth - mContentViewWidth);
    	}
    	if(!isShowing()) {
    		try {
    			showAtLocation(parent, Gravity.LEFT, 0, 0);
			} catch (Exception e) {
				DKLog.e(TAG, e.getLocalizedMessage());
			}
    	}
    }
    
    public void showAllView(View parent, boolean doAnimation) {
    	mDoAnimation = doAnimation;
    	mCurrentMode = MODE_ALL;
    	updateViewWidth();
    	if(doAnimation) {
    		playAnimation(getInAnimatorAll());
    	} else {
    		mContentView.setTranslationX(0);
    	}
    	if(!isShowing()) {
    		try {
    			showAtLocation(parent, Gravity.LEFT, 0, 0);
			} catch (Exception e) {
				DKLog.e(TAG, e.getLocalizedMessage());
			}
    	}
    }

    public void dismiss(boolean doAnimation) {
    	updateViewWidth();
    	if(doAnimation) {
    		playAnimation(getOutAnimationAll());
    	} else {
    		mContentView.setTranslationX(-mContentViewWidth);
    		try {
    			dismiss();
			} catch (Exception e) {
				DKLog.d(TAG, e.getLocalizedMessage());
			}
    	}
    }
    
    private void updateViewWidth() {
    	if(mPartView != null) {
    		mPartViewWidth = mPartView.getMeasuredWidth();
    	}
    	mContentViewWidth = mContentView.getMeasuredWidth();
		mCurrentXPosition = (int) mContentView.getTranslationX();
    }
  
    private void playAnimation(Animator animator) {
        if (!mIsAnimationPlaying) {
            animator.start();
        }
    }

    private Animator getInAnimatorPart() {
    	updateViewWidth();
        Animator contentAnim = ObjectAnimator.ofFloat(mContentView, "translationX", 
        		mCurrentXPosition, mPartViewWidth - mContentViewWidth);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(ANIMATE_DURATION);
        animSet.play(contentAnim);
        animSet.addListener(mInAnimListener);
        return animSet;
    }
    
    private Animator getInAnimatorAll() {
    	updateViewWidth();
        Animator contentAnim = ObjectAnimator.ofFloat(mContentView, "translationX", 
        		mCurrentXPosition, 0);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(ANIMATE_DURATION);
        animSet.play(contentAnim);
        animSet.addListener(mInAnimListener);
        return animSet;
    }
    
    private Animator getOutAnimationAll() {
    	updateViewWidth();
        Animator contentAnim = ObjectAnimator.ofFloat(mContentView, "translationX", 
        		mCurrentXPosition, -mContentViewWidth);
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(ANIMATE_DURATION);
        animSet.play(contentAnim);
        animSet.addListener(mOutAnimListener);
        return animSet;
    }
    
    private BaseAnimatorListener mInAnimListener = new BaseAnimatorListener() {
       
    };

    private BaseAnimatorListener mOutAnimListener = new BaseAnimatorListener() {
        @Override
        public void onAnimationEnd (Animator animation) {
            super.onAnimationEnd(animation);
            try {
    			dismiss();
			} catch (Exception e) {
				DKLog.d(TAG, e.getLocalizedMessage());
			}
        }
        
        public void onAnimationCancel(Animator animation) {
        	try {
    			dismiss();
			} catch (Exception e) {
				DKLog.d(TAG, e.getLocalizedMessage());
			}
        };
    };

    private class BaseAnimatorListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationStart (Animator animation) {
            mIsAnimationPlaying = true;
        }

        @Override
        public void onAnimationEnd (Animator animation) {
            mIsAnimationPlaying = false;
        }
        
        @Override
        public void onAnimationCancel(Animator animation) {
        	mIsAnimationPlaying = false;
        }
    }

    private class RootPanel extends FrameLayout {
        public RootPanel(Context context) {
            super(context);
            setClickable(true);
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            updateViewWidth();
            if(mDoAnimation) {
            	if(mCurrentMode == MODE_PARTY) {
            		playAnimation(getInAnimatorPart());
            	} else if(mCurrentMode == MODE_ALL) {
            		playAnimation(getInAnimatorAll());
            	}
            } else {
            	if(mCurrentMode == MODE_PARTY) {
            		mContentView.setTranslationX(mPartViewWidth - mContentViewWidth);
            	} else if(mCurrentMode == MODE_ALL) {
            		mContentView.setTranslationX(0);
            	}
            }
        }
        
        @Override
        public boolean performClick() {
            return true;
        }
    }
}
