package com.miui.video.widget.actionmode;

import java.util.ArrayList;

import com.miui.video.R;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem.ActionModeItemClickListener;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 *@author tangfuling
 *
 */

public class ActionModeBottomMenu extends LinearLayout {
	
	private ActionModeItemClickListener mListener;
	
	private ArrayList<ActionModeBottomMenuItem> mMenuItems = new ArrayList<ActionModeBottomMenuItem>();
	private static final int ANIMATE_DURATION = 100;
	
	//flags
	private boolean mIsAnimationPlaying;
	private int mAnimationCount;
	
	public ActionModeBottomMenu(Context context) {
		super(context);
		
		init();
	}
	
	public void addItem(ActionModeBottomMenuItem menuItem) {
		if(menuItem == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		int childCount = getChildCount();
		if(childCount != 0) {
			params.leftMargin = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_menu_item_left_margin);
		}
		addView(menuItem, params);
		mMenuItems.add(menuItem);
		
		if(mListener != null) {
			menuItem.setActionModeItemClickListener(mListener);
		}
	}
	
	protected void setIsEnabled(boolean enabled) {
		for(int i = 0; i < mMenuItems.size(); i++) {
			ActionModeBottomMenuItem menuItem = mMenuItems.get(i);
			if(menuItem != null) {
				menuItem.setIsEnabled(enabled);
			}
		}
	}
	
	protected void animationIn() {
		if(mIsAnimationPlaying) {
			return;
		}
		for(int i = 0; i < mMenuItems.size(); i++) {
			ActionModeBottomMenuItem menuItem = mMenuItems.get(i);
			if(menuItem != null) {
				Animator animator = ObjectAnimator.ofFloat(menuItem, "translationY", 
						menuItem.getHeight(), 0);
				animator.setInterpolator(new DecelerateInterpolator());
				animator.setDuration(ANIMATE_DURATION * (i + 1));
				animator.addListener(mAnimatorListener);
				animator.start();
			}
		}
	}
	
	protected void setActionModeItemClickListener(ActionModeItemClickListener listener) {
		this.mListener = listener;
		
		int childCount = getChildCount();
		for(int i = 0; i < childCount; i++) {
			ActionModeBottomMenuItem menuItem = (ActionModeBottomMenuItem) getChildAt(i);
			if(menuItem != null) {
				menuItem.setActionModeItemClickListener(mListener);
			}
		}
	}
	
	//init
	private void init() {
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	//UI callback
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			mIsAnimationPlaying = true;
			mAnimationCount++;
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
	};
}
